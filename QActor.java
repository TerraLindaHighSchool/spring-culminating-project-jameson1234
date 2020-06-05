import greenfoot.*;
public abstract class QActor extends Actor
{
    protected static final int QVAL = 100; // smoothness value
    /** bounds action value -- indicates no restrictions */
    protected static final int UNBOUND = 0;
    /** bounds action value -- indicates no movement beyond bounds */
    protected static final int LIMIT = 1;
    /** bounds action value -- indicates actor is removed upon reaching bounds */
    protected static final int REMOVE = 2;
    /** bounds action value -- indicates actor is transported to opposite world edge upon reaching bounds */
    protected static final int WRAP = 3;
    /** bounds action value -- indicates actor turns toward center upon reaching bounds */
    protected static final int BOUNCE = 4;

    private int qX, qY; // fine-tuned location coordinates (100x)
    private int vX, vY; // fine-tuned speeds along the horizontal axis and the vertial axis (100x)
    private int qR; // fine-tuned rotation (100x)
    private int boundedAction, boundedRange; // world edge fields
    private boolean rotationalBounce;
    
    /**
     * as the main method for movement, uses the current horizontal and vertical speed values to
     * relocate the actor, then checks the bounds of the actor to perform set actions; all 'move'
     * methods call this method and can be called by subclasses for continuous movement along the
     * same direction; the other methods that use it are 'move(int)', which will move the actor in the
     * direction it is facing and 'move(int, int)', which will move the actor in the direction given. 
     */
    public void move()
    {
        qX += vX; // adjust x coordinate value of q-location
        qY += vY; // adjust y coordinate value of q-locaation
        super.setLocation(qX/QVAL, qY/QVAL); // set current location of actor
        boundsAct(); // check for and perform bounded edge action
        rotationalBounce = false;
    }
    
    /**
     * overrides the Actor class 'setLocation(int, int)' method to allow the q-location values to be corrected
     *
     * @param x the world x location to place the actor
     * @param y the world y location to place the actor
     */
    public void setLocation(int x, int y)
    {
        super.setLocation(x, y); // set location of actor
        qX = getX()*QVAL; // set x coordinate value of q-location
        qY = getY()*QVAL; // set y coordinate value of q-location
    }
    
    /**
     * overrides the Actor class 'turn(int)' method to allow the q-rotation value to be adjusted
     *
     * @param amount the change in rotation as a fine-tuned value (100x)
     */
    public void turn(int amount)
    {
        qR = (qR+amount+360*QVAL)%(360*QVAL); // adjust q-rotation value
        super.setRotation(qR/QVAL); // set current rotation
    }
    
    /**
     * overrides the Actor class 'turnTowards(int, int)' method to allow the q-rotation value to be corrected
     *
     * @param x the world x coordinate of the point to face
     * @param y the world y coordinate of the point to face
     */
    public void turnTowards(int x, int y)
    {
        setQRotation((int)(QVAL*(Math.atan2(y-getY(), x-getX())*180/Math.PI)));
    }
    
    /**
     * overrides the Actor class 'setRotation(int)' method to allow the q-rotation value to be corrected
     *
     * @param angle the angle in degrees at which the rotation of the actor is to be set
     */
    public void setRotation(int angle)
    {
        super.setRotation(angle); // set actor rotation
        qR = getRotation()*QVAL; // set q-rotation value
    }
    
    /**
     * adds a force to the movement of the actor
     *
     * @param amount the strength of the force as a fine-tuned value (100x)
     * @param direction the direction of the force in fine-tuned degrees (100x)
     */
    public void addForce(int amount, int direction)
    {
        vX += Math.cos((double)direction*Math.PI/(180*QVAL))*(double)amount; // new horizontal speed
        vY += Math.sin((double)direction*Math.PI/(180*QVAL))*(double)amount; // new vertical speed
    }
    
    /**
     * applys a force to (moves) the actor
     * 
     * @param amount the strength of the force as a fine-tuned value (100x)
     * @param direction the direction of the force in fine-tuned degrees (100x)
     */
    public void move(int amount, int direction)
    {
        int holdX = vX, holdY = vY; // save the current values
        vX = 0; vY = 0; // clear the values
        addForce(amount, direction);
        move();
        vX = holdX; vY = holdY; // restore saved values
    }
    
    /**
     * overrides the Actor class 'move(int)' method to use the given fine-tuned distance with the
     * fine-tuned rotation of the actor for the direction
     * 
     * @param amount the fine-tuned distanc value to move (100x)
     */
    public void move(int amount)
    {
        move(amount, qR);
        rotationalBounce = true;
    }
    
    /**
     * sets the action and range fields for behavior when bounds are exceeded by actor
     *
     * @param action one of the bounds action values of this class
     * @param range offset from edge the action is to occur
     */
    public void setBoundedAction(int action, int range)
    {
        boundedAction = action%5; // the given action
        boundedRange = range; // the given range offset where positive direction is away from center
    }
    
    /**
     * internal method to perform the bounded action that is currently set to the actor
     */
    private void boundsAct()
    {
        switch(boundedAction)
        {
            case UNBOUND: // no bounded action taken
                break;
            case LIMIT: // no movement beyond bounds
                if (qX <= -boundedRange*QVAL)
                {
                    setQLocation(-boundedRange*QVAL, qY);
                    if (vX < 0) vX = 0;
                }
                if (qY <= -boundedRange*QVAL)
                {
                    setQLocation(qX, -boundedRange*QVAL);
                    if (vY < 0) vY = 0;
                }
                if (qX >= (getWorld().getWidth()+boundedRange-1)*QVAL)
                {
                    setQLocation((getWorld().getWidth()+boundedRange-1)*QVAL, qY);
                    if (vX > 0) vX = 0;
                }
                if (qY >= (getWorld().getHeight()+boundedRange-1)*QVAL)
                {
                    setQLocation(qX, (getWorld().getHeight()+boundedRange-1)*QVAL);
                    if (vY > 0) vY = 0;
                }
                break;
            case REMOVE: // actor is removed at bounds
                if (getX() <= -boundedRange ||
                    getX() >= getWorld().getWidth()+boundedRange-1 ||
                    getY() <= -boundedRange ||
                    getY() >= getWorld().getHeight()+boundedRange-1)
                        getWorld().removeObject(this);
                break;
            case WRAP: // actor is transported to opposite world edge at bounds
                if (getX() <= -boundedRange)
                    setLocation(getX()+getWorld().getWidth()+boundedRange*2-2, getY());
                else if (getY() <= -boundedRange)
                    setLocation(getX(), getY()+getWorld().getHeight()+boundedRange*2-2);
                else if (getX() >= getWorld().getWidth()+boundedRange-1)
                    setLocation(1-boundedRange, getY());
                else if (getY() >= getWorld().getHeight()+boundedRange-1)
                    setLocation(getX(), 1-boundedRange);
                break;
            case BOUNCE: // actor faces toward center at bounds
                if (rotationalBounce)
                {
                    if ((getX() <= -boundedRange && qR > 9000 && qR < 27000) || (getX() >= getWorld().getWidth()+boundedRange-1 && (qR < 9000 || qR > 27000)))
                    {
                        setQRotation((54000-qR)%36000);
                    }
                    if ((getY() <= -boundedRange && qR > 18000) || (getY() >= getWorld().getHeight()+boundedRange-1 && qR < 18000))
                    {
                        setQRotation((72000-qR)%36000);
                    }
                }
                else
                {
                    if ((getX() <= -boundedRange && vX < 0) || (getX() >= getWorld().getWidth()+boundedRange-1 && vX > 0)) vX = -vX;
                    if ((getY() <= -boundedRange && vY < 0) || (getY() >= getWorld().getHeight()+boundedRange-1 && vY > 0)) vY = -vY;
                }
                break;
        }
    }
    
    /**
     * sets the location of the actor to fined-tuned coordinate values
     * 
     * @param x the fine-tuned x-coordinate value (100x)
     * @param y the fine-tuned y-coordinate value (100x)
     */
    public void setQLocation(int x, int y)
    {
        qX = x;
        qY = y;
        super.setLocation(qX/QVAL, qY/QVAL);
    }
    
    /**
     * sets the rotation of the actor to a fined-tuned value
     * 
     * @param amount the fine-tuned rotational value for the actor in degrees (100x)
     */
    public void setQRotation(int amount)
    {
        qR = amount;
        super.setRotation(qR/QVAL);
    }
    
    /**
     * returns the fine-tuned x-coordinate for the locationof the actor (100x)
     * 
     * @return the fine-tuned value of the x-coordinate for the location of the actor
     */
    protected int getQX() { return qX; }
    
    /**
     * returns the fine-tuned y-coordinate for the location of the actor (100x)
     * 
     * @return the fine-tuned value of the y-coordinate for the location of the actor
     */
    protected int getQY() { return qY; }
    
    /**
     * returns the fine-tuned rotation of the actor (100x)
     * 
     * @return the fine-tuned rotation of the actor
     */
    protected int getQR() { return qR; }
    
    /**
     * returns the fine-tuned speed along the horizontal (100x)
     * 
     * @return the fine-tuned value of the speed of the actor along the horizontal
     */
    protected int getVX() { return vX; }
    
    /**
     * returns the fine-tuned speed along the vertical (100x)
     * 
     * @return the fine-tuned value of the speed of the actor along the vertical
     */
    protected int getVY() { return vY; }
    
    /**
     * sets the horizontal speed to the given fine-tuned value (100x)
     * 
     * @param speed the fine-tuned value the horizontal speed is to be set to
     */
    protected void setVX(int speed) { vX = speed; }
    
    /**
     * sets the vertical speed to the given fine-tuned value (100x)
     * 
     * @param speed the fine-tuned value the horizontal speed is to be set to
     */
    protected void setVY(int speed) { vY = speed; }
}