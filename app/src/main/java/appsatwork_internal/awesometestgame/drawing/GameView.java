package appsatwork_internal.awesometestgame.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import appsatwork_internal.awesometestgame.R;

/**
 * Created by Casper on 1-2-2015.
 */
public class GameView extends View
{
    private Paint brush;
    private final int starCount = 600;
    public static Random rng;
    private Star[] stars = new Star[starCount];
    private boolean loaded = false;
    private float starFadeRate = 0.014f;
    private Asteroid asteroid;
    public Spaceship Ship;
    private List<Bullet> Bullets = new ArrayList<Bullet>();

    public GameView(Context context, AttributeSet set)
    {
        super(context, set);
    }

    private void Load()
    {
        InitializeBrush();
        InitializeStars();
        InitializeAsteroid();
        InitializeShip();
        Bullet.LoadSprite(getContext());
        loaded = true;
    }

    private void InitializeBrush()
    {
        brush = new Paint();
        brush.setAlpha(255);
    }

    private void InitializeStars()
    {
        rng = new Random();
        for(int i = 0; i < starCount; i++)
        {
            int width = getWidth();
            int height = getHeight();
            int x = rng.nextInt(width);
            int y = rng.nextInt(height);
            int r = rng.nextInt(5);
            float a = rng.nextFloat()/2f + 0.25f;
            stars[i] = new Star(r,x,y,a);
        }
    }

    private void InitializeAsteroid()
    {
        asteroid = new Asteroid(getWidth()/2, getHeight()/2);
        asteroid.Sprite = BitmapFactory.decodeResource(getResources(), R.drawable.asteroid);
    }

    private void InitializeShip()
    {
        Ship = new Spaceship();
        Ship.LoadSprite();
    }

    private void Update()
    {
        //If this is the first time ever to update, load the stars and such
        if(!loaded)
            Load();

        //Update the star fade in/out
        for(int i = 0; i < starCount; i++)
            stars[i].Update();

        //Update the bullet positions
        for(int i = 0; i < this.Bullets.size(); i++)
            Bullets.get(i).Move();

        //Update the asteroid position
        asteroid.Update();
    }

    public synchronized void onDraw(Canvas canvas)
    {
        Update();

        DrawBackground(canvas);

        //Draw the stars
        for(int i = 0; i < starCount; i++)
            stars[i].Draw(canvas, brush);

        //Draw the bullets
        for(int i = 0; i < Bullets.size(); i++)
            Bullets.get(i).Draw(canvas, brush);

        //Draw the ship
        Ship.Draw(canvas, brush);

        //Draw the asteroid
        asteroid.Draw(canvas, brush);
    }

    private void DrawBackground(Canvas canvas)
    {
        //Set our brush parameters for the background
        brush.setColor(Color.BLACK);
        brush.setStrokeWidth(1);

        //Draw the background
        canvas.drawRect(0,0, getWidth(), getHeight(), brush);
    }

    public class Spaceship
    {
        Vector<Float> pos = new Vector<Float>(2);
        Vector<Float> direction = new Vector<Float>(2);
        int speed = 6;
        Bitmap sprite;
        Matrix transformation = new Matrix();

        public Spaceship()
        {
            direction.addElement(1.0f); direction.addElement(1.0f); Normalize(direction, speed);
        }

        public void Fire()
        {
            Vector<Float> velocityVector = new Vector<Float>(2);
            velocityVector.addElement(direction.get(0));
            velocityVector.addElement(direction.get(1));

            Normalize(velocityVector, 0.4f*Bullet.sprite.getWidth()/2.0f);

            Vector<Float> positionVector = new Vector<Float>(2);

            int sign = direction.get(1) > 0 ? 1 : -1;
            int sign2 = direction.get(0) > 0 ? 1 : -1;

            positionVector.addElement(pos.get(0) + sign * velocityVector.get(0));
            positionVector.addElement(pos.get(1) + sign2 * velocityVector.get(1));

            Bullets.add(new Bullet(positionVector, velocityVector, 25));
        }


        public void MoveToward(PointF point)
        {
            direction.setElementAt(point.x - pos.get(0), 0);
            direction.setElementAt(point.y - pos.get(1), 1);

            Normalize(direction, speed);

            pos.setElementAt(pos.get(0) + direction.get(0), 0);
            pos.setElementAt(pos.get(1) + direction.get(1), 1);

        }


        private void UpdateMatrix()
        {
            transformation.reset();
            transformation.postRotate(Angle(direction), sprite.getWidth()/2, sprite.getHeight()/2);
            transformation.postScale(0.4f, 0.4f);
            transformation.postTranslate(pos.get(0)-0.4f * sprite.getWidth()/2, pos.get(1)-0.4f * sprite.getHeight()/2);
        }

        private float Angle(Vector<Float> vec1)
        {
            float value = (float)Math.toDegrees(Math.atan2(vec1.get(1),vec1.get(0)));
            return value+90;
        }

        public void LoadSprite()
        {
            sprite = BitmapFactory.decodeResource(getResources(), R.drawable.ship);
            pos.addElement(getWidth()/2.0f-0.4f * sprite.getWidth()/2); pos.addElement(getHeight()/2.0f-0.4f * sprite.getHeight()/2);
        }

        public void Draw(Canvas canvas, Paint brush)
        {
            UpdateMatrix();
            //Get alpha so we can reset it.
            int alpha = brush.getAlpha();

            //Set alpha to fade the star in/out and set the color
            brush.setAlpha(255);

            //Draw a star (which is a circle).
            canvas.drawBitmap(sprite, transformation, brush);

            //Reset the alpha and the color
            brush.setAlpha(alpha);
        }
    }

    private class Asteroid
    {
        public int X = -1;
        public int Y = -1;
        public PointF Direction = new PointF(-0.5f,0.5f);
        public Bitmap Sprite;
        public int Velocity = 10;
        public Matrix Transformation = new Matrix();

        public Asteroid(int x, int y)
        {
            X = x;
            Y = y;
        }

        public void Update()
        {
            Transformation.reset();

            if(X + (Sprite.getWidth()/2) > getWidth() || X - (Sprite.getWidth()/2) < 0)
            {
                Direction.x *= -1;
            }
            if(Y + (Sprite.getHeight()/2) > getHeight() || Y - (Sprite.getHeight()/2) < 0)
            {
                Direction.y *= -1;
            }

            X += Direction.x * Velocity;
            Y += Direction.y * Velocity;

            Transformation.postTranslate(X-Sprite.getWidth()/2, Y - Sprite.getHeight()/2);
        }

        public void Draw(Canvas canvas, Paint brush)
        {
            //Remember what this was set to before calling
            int alpha = brush.getAlpha();
            //Make sure our asteroid is not transparent
            brush.setAlpha(255);
            //Draw the sprite with (X,Y) as the center.
            canvas.drawBitmap(Sprite, Transformation, brush);
            //Reset the alpha
            brush.setAlpha(alpha);
        }
    }

    private class Star
    {
        public float Visibility = 1.0f;
        public int Sign = -1;

        public int Radius = -1;
        public int X = -1;
        public int Y = -1;

        public Star(int radius, int x, int y, float alpha)
        {
            Radius = radius;
            X = x;
            Y = y;
            Visibility = alpha;
            Sign = alpha < 0.5f ? -1 : 1;
        }

        public void Update()
        {
            if(Visibility < 0.1f || Visibility > 0.9f)
                Sign *= -1;

            Visibility += Sign * starFadeRate;
        }

        public void Draw(Canvas canvas, Paint brush)
        {
            //Get color so we can reset it.
            int color = brush.getColor();

            //Get alpha so we can reset it.
            int alpha = brush.getAlpha();

            //Set alpha to fade the star in/out and set the color
            brush.setColor(Color.WHITE);
            brush.setAlpha((int)(Visibility * 255.0f));

            //Draw a star (which is a circle).
            canvas.drawCircle(X, Y, Radius, brush);

            //Reset the alpha and the color
            brush.setAlpha(alpha);
            brush.setColor(color);
        }
    }


    public void Normalize(Vector<Float> vector, float finalLength)
    {
        float length = 0;
        for (float element : vector)
            length += element*element;
        length = (float)Math.sqrt((double)length);

        for(int i = 0; i < vector.capacity(); i++)
            vector.setElementAt((vector.get(i)/length)*finalLength, i);
    }
}