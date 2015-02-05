package appsatwork_internal.awesometestgame;

import appsatwork_internal.awesometestgame.drawing.GameView;
import appsatwork_internal.awesometestgame.util.SystemUiHider;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainActivity extends Activity
{
    private Handler frame = new Handler();
    private final int FPS = 60;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
      //  requestWindowFeature(Window.FEATURE_NO_TITLE);
      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
      //          WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
    }

    public void fire_button_click(View view)
    {
        gameView.Ship.Fire();
    }

    public void exit_button_click(View view)
    {
        this.finish();
    }

    @Override
    protected void onPostCreate(Bundle bundle)
    {
        super.onPostCreate(bundle);
        frame.postDelayed(frameUpdate, FPS);
        gameView = ((GameView)findViewById(R.id.the_canvas));
        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gameView.Ship.MoveToward(new PointF(event.getX(), event.getY()));

                return true;
            }
        });
    }

    private Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {
            frame.removeCallbacks(frameUpdate);
            //make any updates to on screen objects here
            //then invoke the on draw by invalidating the canvas
            gameView.invalidate();
            frame.postDelayed(frameUpdate, (int)(1.0f/(float)FPS * 1000.0f));
        }
    };

}
