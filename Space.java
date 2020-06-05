import greenfoot.*;

public class Space extends World
{
    static Actor levelDisplay, scoreDisplay;
    static int level, score;
    private Star[] stars = new Star[210];
    Color color1 = new Color(220,50,200);
    Color color2 = new Color(100,50,220);
    Color color3 = new Color(255,0,0);

    public Space()
    {
        super(600, 600, 1, false);
        // the background
        getBackground().setColor(Color.BLACK);
        getBackground().fill();

        star();
        // the stat actors
        level = 0;
        score = 0;
        levelDisplay = new Generic();
        scoreDisplay = new Generic();
        adjustScore(0);
        nextLevel(this);
        // prepare
        addObject(levelDisplay, 80, 15);
        addObject(scoreDisplay, 520, 15);           
        addObject(new Rocket(), 300, 300);
        prepare();
    }

    private void star()
    {
        for(int i = 0; i < 210; i++){
            Star star;
            int deltaSpeed = Greenfoot.getRandomNumber(2);

            if(i < 70)
            {
                star = new Star(-1 - deltaSpeed, color1, getWidth(), getHeight());
                addObject(star, star.getX(), star.getY());
                stars[i] = star;
            }

            if(i >= 70 && i < 140)
            {
                star = new Star(-3 - deltaSpeed, color2, getWidth(), getHeight());
                addObject(star, star.getX(), star.getY());
                stars[i] = star;
            }

            if(i >= 140)
            {
                star = new Star(-5 - deltaSpeed, color3, getWidth(), getHeight());
                addObject(star, star.getX(), star.getY());
                stars[i] = star;
            }
        }
    }

    public void act()
    {
        // check level end
        if (getObjects(Asteroid.class).isEmpty()) nextLevel(this);
        // check died
        if (getObjects(Rocket.class).isEmpty() && getObjects(Explosion.class).isEmpty())
        {
            removeObject(levelDisplay);
            removeObject(scoreDisplay);
            String text = "Game Over\n\nFinal Score\n"+score;
            GreenfootImage endGame = new GreenfootImage(text, 64, Color.RED, new Color(0, 0, 0, 0));
            getBackground().drawImage(endGame, 300-endGame.getWidth()/2, 300-endGame.getHeight()/2);

        }
        for(int i = 0; i < 210; i++)
        {
            if(stars[i] != null)
            {
                stars[i].move();
            }
        }
    }

    // asteroid adder
    private void addAsteroids()
    {
        for (int i=0; i<level+1; i++)
        {
            int x = (Greenfoot.getRandomNumber(2)+400)%401+50;
            int y = (Greenfoot.getRandomNumber(2)+400)%401+50;
            addObject(new Asteroid(), x, y);
        }
    }

    // score changer/displayer
    public static void adjustScore(int adjustment)
    {
        score += adjustment;
        scoreDisplay.setImage(new GreenfootImage("Score: "+score, 24, Color.YELLOW.brighter(), new Color(0, 0, 0, 0)));
    }

    // level changer/displayer
    public static void nextLevel(Space space)
    {
        level++;
        levelDisplay.setImage(new GreenfootImage("Level: "+level, 24, Color.YELLOW .brighter(), new Color(0, 0, 0, 0)));
        space.addAsteroids();
    }

    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    private void prepare()
    {
    }
}
