import greenfoot.*;

public class Asteroid extends QActor
{
    int stage;
    int turnRate;
    
    // create initial large asteroid
    public Asteroid()
    {
        this(0);
    }
    
    // create sized asteroid
    public Asteroid(int smaller)
    {
        // the QActor bounded action settings
        setBoundedAction(QActor.WRAP, 0);
        // the QActor speed and direction settings
        addForce(Greenfoot.getRandomNumber(2*QVAL)+1*QVAL, Greenfoot.getRandomNumber(360*QVAL));
        // the image
        stage = smaller;
        int size = 60-20*smaller;
        GreenfootImage image = new GreenfootImage("rock.png");
        image.scale(size, size);
        setImage(image);
        // the tumble rate
        turnRate = Greenfoot.getRandomNumber(10*QVAL+1)-5*QVAL; // QVAL is smoothness value
    }

    public void act()
    {
        // move and turn
        turn(turnRate);
        move();
        // shot check
        if (!getObjectsInRange(30-10*stage, Rocket.Shot.class).isEmpty())
        {
            Actor shot = (Actor)getObjectsInRange(30-10*stage, Rocket.Shot.class).get(0);
            Space.adjustScore(20+20*stage); // scoring :: large:20; medium:40; small:60
            // check fragmentation
            if (stage < 2)
            {
                for (int i=0; i<2; i++)
                {
                    Asteroid asteroid = new Asteroid(stage+1);
                    getWorld().addObject(asteroid, getX(), getY());
                    asteroid.move((15-stage*5)*QVAL);
                }
            }
            getWorld().removeObject(shot);
            getWorld().removeObject(this);
            return;
        }
        // shield check
        if (!getObjectsInRange(getImage().getWidth()/2+30, Rocket.Shield.class).isEmpty())
            ((Rocket.Shield)getObjectsInRange(getImage().getWidth()/2+30, Rocket.Shield.class).get(0)).bumpAsteroid(this);
    }
}
