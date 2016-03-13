package edu.temple.homedrone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Eric on 2/4/2016.
 */
public class Triangle
{

    private final int mProgram;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private int mPositionHandle;
    private int mColorHandle;

    // number of coordinates per vertex in this array
    static final int   COORDS_PER_VERTEX = 3;
    static       float triangleCoords[]  = {   // in counterclockwise order:
            0.0f, 0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f  // bottom right
    };

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };


    private final int vertexCount  = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public Triangle()
    {

        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4 );
        // use the device hardware's native byte order
        bb.order( ByteOrder.nativeOrder() );

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put( triangleCoords );
        // set the buffer to read the first coordinate
        vertexBuffer.position( 0 );

        int vertexShader = MyGLRenderer.loadShader( GLES20.GL_VERTEX_SHADER,
                vertexShaderCode );
        int fragmentShader = MyGLRenderer.loadShader( GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode );

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader( mProgram, vertexShader );

        // add the fragment shader to program
        GLES20.glAttachShader( mProgram, fragmentShader );

        // creates OpenGL ES program executables
        GLES20.glLinkProgram( mProgram );
    }


    public void draw()
    {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram( mProgram );

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation( mProgram, "vPosition" );

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray( mPositionHandle );

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer( mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer );

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation( mProgram, "vColor" );

        // Set color for drawing the triangle
        GLES20.glUniform4fv( mColorHandle, 1, color, 0 );

        // Draw the triangle
        GLES20.glDrawArrays( GLES20.GL_TRIANGLES, 0, vertexCount );

        // Disable vertex array
        GLES20.glDisableVertexAttribArray( mPositionHandle );

    }

    private FloatBuffer vertexBuffer; // buffer holding the vertices
    private float vertices[] = { -1.0f, -1.0f, 0.0f, // V1 - bottom left
            -1.0f, 1.0f, 0.0f, // V2 - top left
            1.0f, -1.0f, 0.0f, // V3 - bottom right
            1.0f, 1.0f, 0.0f // V4 - top right
    };

    private FloatBuffer textureBuffer; // buffer holding the texture coordinates
    private float texture[] = {
            // Mapping coordinates for the vertices
            0.0f, 1.0f, // top left (V2)
            0.0f, 0.0f, // bottom left (V1)
            1.0f, 1.0f, // top right (V4)
            1.0f, 0.0f // bottom right (V3)
    };

    /** The texture pointer */
    int[] textures = new int[1];

    public void loadGLTexture( GL10 gl, Context context )
    {
        System.out.println( "loaded" );
        // // loading texture
        Bitmap bitmap = BitmapFactory.decodeResource( context.getResources(),
                R.drawable.logo );
//here is the problem when ever i command bitmap it will be working .with out command its //showing null pointer exception
        // generate one texture pointer
        gl.glGenTextures( 1, textures, 0 );
        // ...and bind it to our array
        gl.glBindTexture( GL10.GL_TEXTURE_2D, textures[ 0 ] );

        // create nearest filtered texture
        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST );
        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR );

        // Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_REPEAT );
        gl.glTexParameterf( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_REPEAT );

        // Use Android GLUtils to specify a two-dimensional texture image from
        // our bitmap
        GLUtils.texImage2D( GL10.GL_TEXTURE_2D, 0, bitmap, 0 );
        // //
        // // // Clean up
        bitmap.recycle();
    }

}
