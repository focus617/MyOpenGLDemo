package com.focus617.myopengldemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.focus617.myopengldemo.render.XGLRender.Companion.Shape

class MainActivity : AppCompatActivity() {

    private lateinit var mGLSurfaceView: XGLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 创建一个GLSurfaceView实例,并将其设置为此Activity的ContentView。
        mGLSurfaceView = XGLSurfaceView(this)
        setContentView(mGLSurfaceView)
    }

    override fun onResume() {
        super.onResume()
        // 恢复渲染线程，如果有必要的话重新创建OpenGL上下文，它和onPause对应
        mGLSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        // 暂停渲染线程
        mGLSurfaceView.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu items for use in the action bar
        val inflater = menuInflater;
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_triangle -> {
                Toast.makeText(this, "Triangle", Toast.LENGTH_SHORT).show()
                mGLSurfaceView.setupShape(Shape.Triangle)
                true
            }
            R.id.action_square -> {
                Toast.makeText(this, "Square", Toast.LENGTH_SHORT).show()
                mGLSurfaceView.setupShape(Shape.Square)
                true
            }
            R.id.action_cube -> {
                Toast.makeText(this, "Cube", Toast.LENGTH_SHORT).show()
                mGLSurfaceView.setupShape(Shape.Cube)
                true
            }
            R.id.action_air_hockey -> {
                Toast.makeText(this, "AirHockey", Toast.LENGTH_SHORT).show()
                mGLSurfaceView.setupShape(Shape.AirHockey)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}