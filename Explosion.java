import greenfoot.*;

public class Explosion extends Actor
{
    // the image
    private static GreenfootImage img;
    static
    {
        img = new GreenfootImage(40, 40);
        int r = 255, g = 128, b = 0, a = 128;
        for (int i=19; i>=4; i--, r-=7, g-=7, a-=7)
        {
            img.setColor(new Color(r, g, b, a));
            img.fillOval(20-i, 20-i, i*2, i*2);
        }
    }
    
    int age;
    
    public Explosion()
    {
        GreenfootImage image = new GreenfootImage(img);
        image.scale(7, 7);
        setImage(image);
    }
    
    public void act()
    {
        // expiring
        if ((++age) == 60)
        {
            getWorld().removeObject(this);
            return;
        }
        // expanding
        if (age <= 15)
        {
            GreenfootImage image = new GreenfootImage(img);
            image.scale(10+age*3, 10+age*3);
            setImage(image);
        }
        else
        // disapating
        {
            GreenfootImage image = new GreenfootImage(img);
            image.scale(80-age, 80-age);
            setImage(image);
        }
    }
}