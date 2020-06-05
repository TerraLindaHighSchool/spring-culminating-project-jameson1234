import greenfoot.*;

public class Rocket extends QActor
{
    // the images
    private static final GreenfootImage floating = new GreenfootImage("rocket-idle.png"),
                                        thrusting = new GreenfootImage("rocket.png"),
                                        shieldImg = new GreenfootImage(60, 60),
                                        shieldBarBase = new GreenfootImage(80, 10);
    static
    {
        floating.scale(floating.getWidth()/3, floating.getHeight()/3);
        thrusting.scale(thrusting.getWidth()/3, thrusting.getHeight()/3);
        shieldImg.setColor(Color.BLUE);
        for (int i=0; i<45; i++)
        {
            shieldImg.rotate(8);
            shieldImg.fillRect(55, 28, 4, 4);
        }
        shieldImg.drawOval(0, 0, 59, 59);
        shieldImg.setTransparency(75);
        shieldBarBase.setColor(Color.RED);
        shieldBarBase.drawRect(0, 0, 79, 9);
    }
    // the thrust value
    private static final int thrust = 5; // (1/20 pixel/act)/act acceleration
    
    private boolean shotKeyDown;
    private int shieldUsage;
    private Shield shield;
    private ShieldBar shieldBar;
    
    public Rocket()
    {
        setImage(floating); // initial image
        setBoundedAction(QActor.WRAP, 0); // sets QActor bounded action values
        shieldBar = new ShieldBar();
        shieldBar.updateImage();
    }
    
    protected void addedToWorld(World world)
    {
        world.addObject(shieldBar, world.getWidth()/2, 15);
    }

    public void act()
    {
        // turning
        int dr = 0;
        if (Greenfoot.isKeyDown("right")) dr++;
        if (Greenfoot.isKeyDown("left")) dr--;
        turn(300*dr); // 3 degree turning
        // moving
        if (Greenfoot.isKeyDown("up"))
        {
            if (getImage() == floating) setImage(thrusting);
            addForce(thrust, getQR()); // acceleration
        }
        else
        {
            if (getImage() == thrusting) setImage(floating);
            // if (getQS() > 0) addForce(-1, getQV()); // drag
        }
        move();
        if (shield != null) shield.setLocation(getX(), getY());
        // dying
        for (Object obj : getIntersectingObjects(Asteroid.class))
        {
            Actor Asteroid = (Actor)obj; 
            int dist = (Asteroid.getImage().getWidth()+(getImage().getWidth()+getImage().getHeight())/2)/2;
            if (Math.hypot(getX()-Asteroid.getX(), getY()-Asteroid.getY()) <= dist)
            {
                getWorld().addObject(new Explosion(), getX(), getY());
                new GreenfootSound("explode.mp3").play();
                getWorld().removeObject(this);
                return;
            }
        }
        // shooting
        if (!shotKeyDown && Greenfoot.isKeyDown("space"))
        {
            (new GreenfootSound("shoot.wav")).play();
            getWorld().addObject(new Shot(), getX(), getY());
            shotKeyDown = true;
        }
        if (shotKeyDown && !Greenfoot.isKeyDown("space")) shotKeyDown = false;
        // shielding
        if (shieldUsage > 0 && shield == null)
        {
            shieldUsage--;
            shieldBar.updateImage();
        }
        if (Greenfoot.isKeyDown("shift") && shieldUsage == 0 && shield == null)
            getWorld().addObject(new Shield(), getX(), getY());
    }
    
    public class Shot extends QActor
    {
        public Shot()
        {
            GreenfootImage image = new GreenfootImage(8, 5);
            image.setColor(Color.ORANGE);
            image.fillOval(0, 0, 7, 3);
            setImage(image);
            turn(Rocket.this.getQR());
            setBoundedAction(QActor.REMOVE, 0);
        }
                
        public void act()
        {
            move(8*QVAL);
        }
    }
    
    public class Shield extends Actor
    {
        public Shield()
        {
            setImage(shieldImg);
            shield = this;
        }
        
        public void act()
        {
            // expend shield
            shieldUsage++;
            shieldBar.updateImage();
            if (shieldUsage/5 == 200)
            {
                getWorld().removeObject(this);
                shield = null;
                return;
            }
            if (shieldUsage/5 >= 150 && shieldUsage%25 == 0) shieldImg.setTransparency(48*((shieldUsage/25)%2));
        }
        
        public void bumpAsteroid(Asteroid Asteroid)
        {
            double angle = Math.atan2(Asteroid.getY()-getY(), Asteroid.getX()-getX());
            double force = Math.hypot(Asteroid.getVX()-Rocket.this.getVX(), Asteroid.getVY()-Rocket.this.getVY())*0.5;
            Rocket.this.addForce(-(int)force, (int)(angle*QActor.QVAL*180/Math.PI));
            Asteroid.addForce((int)force, (int)(angle*QActor.QVAL*180/Math.PI));
        }
    }
    
    public class ShieldBar extends Actor
    {
        public ShieldBar()
        {
            setImage(shieldBarBase);
            updateImage();
        }
        
        public void updateImage()
        {
            GreenfootImage image = new GreenfootImage(shieldBarBase);
            if (shieldUsage == 0)
            {
                image.setColor(Color.BLUE.brighter());
                image.fillRect(2, 2, 76, 6);
            }
            else
            {
                image.setColor(Color.YELLOW);
                image.fillRect(2, 2, 76-76*shieldUsage/1000, 6);
            }
            setImage(image);
        }
    }
}
