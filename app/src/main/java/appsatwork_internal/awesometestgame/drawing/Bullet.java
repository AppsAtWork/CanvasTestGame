package appsatwork_internal.awesometestgame.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.Random;
import java.util.Vector;

import appsatwork_internal.awesometestgame.R;

public class Bullet
{
    public Vector<Float> Position;
    public int Radius;
    public Vector<Float> Direction;
    private Matrix transformation = new Matrix();
    public float Speed;
    public static Bitmap sprite;

    public Bullet(Vector<Float> position, Vector<Float> direction, float speed)
    {
        Position = position;
        Radius = (new Random()).nextInt(5) + 10;
        Direction = direction;
        Speed = speed;
        CalculateMatrix();
    }

    private void CalculateMatrix()
    {
        transformation.reset();
        transformation.postRotate((float)Math.toDegrees(Math.atan(Direction.get(1)/Direction.get(0)))+90);
        transformation.postScale(0.4f, 0.4f);
        transformation.postTranslate(Position.get(0), Position.get(1));
    }

    public static void LoadSprite(Context context)
    {
        sprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet);
    }

    public void Move()
    {
        Position.setElementAt(Position.get(0) + (Direction.get(0) * Speed), 0);
        Position.setElementAt(Position.get(1) + (Direction.get(1) * Speed), 1);
    }

    public void Draw(Canvas canvas, Paint brush)
    {
        CalculateMatrix();
        //Get color so we can reset it.
        int color = brush.getColor();

        //Get alpha so we can reset it.
        int alpha = brush.getAlpha();

        //Set alpha to fade the star in/out and set the color
        brush.setColor(Color.GREEN);
        brush.setAlpha(255);

        //Draw a star (which is a circle).
        canvas.drawBitmap(sprite, transformation, brush);

        //Reset the alpha and the color
        brush.setAlpha(alpha);
        brush.setColor(color);
    }
}
