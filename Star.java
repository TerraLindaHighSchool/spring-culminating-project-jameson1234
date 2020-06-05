import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Creates a parallax illusion of the star field.
 * 
 * @author Bruce Gustin 
 * @version 3/15/2020 v1
 */
public class Star extends Actor
{
    private int x, y, speed, size;
    private Color color;
    
   public int getX()
    {
        return x;
    }
   public int getY()
    {
        return y;
    }
    public Star(int speed, Color color, int worldWidth, int worldHeight)
    {
      size = 1 + Greenfoot.getRandomNumber(4);
      GreenfootImage image = new GreenfootImage (size, size);
      image.setColor (color);
      image.fillOval (0, 0, size, size); 
      setImage(image);
      this.speed = speed;
      
      x = Greenfoot.getRandomNumber(worldWidth);
      y = Greenfoot.getRandomNumber(worldHeight);
    
   }

   public void move()
   {
       x = speed + x;
       setLocation(x + speed, y);
       if(x < 1)
       {
          x = getWorld().getWidth();
       }
       
   }
}
