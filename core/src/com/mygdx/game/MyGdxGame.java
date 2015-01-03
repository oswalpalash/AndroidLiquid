package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.bloom.Bloom;

public class MyGdxGame extends ApplicationAdapter implements InputProcessor{
    //Animation zombie[];

    SpriteBatch batch;
    Sprite sprite,sprite1,sprite2,gun;
    Texture img,img1,img2;
    Image backgroundImage;
    World world;
    Body body,body1;
    Array<Body> water;
    Array<Body> zom;
    //public b2DebugDraw d;
    Array<Sprite> sprites;
    Body bodyEdgeScreen;
    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;
    OrthographicCamera camera;
    BitmapFont font;
    static int count=0;
    float torque = 0.0f;
    boolean drawSprite = true;
    final float PIXELS_TO_METERS = 100f;
    Bloom bloom;
    Stage stage;
    public boolean drawCircles = true;
    public boolean flag=true;
    private float highestY = 0.0f;
    float accelX,accelY;
    //Animation anim;
    Array<Body> bod;
    Sprite zombie0,zombie1,zombie2,zombie3,zombie4,zombie5,zombie6;
    Array<Sprite> zombie;
    int f;
    float wide,high;
	@Override
	public void create () {
        f=1;
        img = new Texture("ball_1.png");
        sprite = new Sprite(img);
        sprite.setSize(10,10);
        img1 = new Texture("ball_2.png");
        img2 = new Texture("backg.jpg");
        gun = new Sprite(new Texture("gun.png"));
        sprite2=new Sprite(img2);
        sprite2.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundImage = new Image(img2);
        zombie=new Array<Sprite>();
        for (int i = 0; i < 7; i++) {
            sprite1=new Sprite(new Texture("zombie/zombie_"+(i+1)+".gif"));
            zombie.add(sprite1);
        }
		batch = new SpriteBatch();
        bloom=new Bloom(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2,true,true,true);
        bloom.setClearColor(0,0,0,0);

        world =new World(new Vector2(0,-0.98f),true);
        bod=new Array<Body>();
        sprites=new Array<Sprite>();
        createEdge();
        int n=0;
        wide=Gdx.graphics.getWidth();
        high=Gdx.graphics.getHeight();

        Gdx.input.setInputProcessor(this);
        debugRenderer = new Box2DDebugRenderer();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.
                getHeight());
	}
    boolean p=false;
    float frameCounter=0;

	@Override
	public void render () {
        camera.update();

        // Step the physics simulation forward at a rate of 60hz
        world.step(1f/5f, 6, 2);
        Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS,
                PIXELS_TO_METERS, 0);

        batch.begin();
        batch.enableBlending();
        sprite2.setPosition(-Gdx.graphics.getWidth()/2,-Gdx.graphics.getHeight()/2);
        sprite2.draw(batch);
        batch.end();

        blurred_scene();
        zombie_render();
        show_gun();
        //debugRenderer.render(world,debugMatrix);


    }
    void show_gun(){
        batch.begin();
        batch.enableBlending();
        gun.setPosition(-Gdx.graphics.getWidth() / 2, -Gdx.graphics.getHeight() / 2);
        gun.setSize(Gdx.graphics.getHeight() / 4, Gdx.graphics.getHeight() / 4);
        gun.draw(batch);
        batch.end();

    }
    float conv_x(float tx){
            tx = tx - Gdx.graphics.getWidth()/2;
            return tx/PIXELS_TO_METERS;
    }
    float conv_y(float ty){
        ty = ty - Gdx.graphics.getHeight()/2;
        return -ty/PIXELS_TO_METERS;
    }
    void blurred_scene()
    {
        bloom.capture();
        batch.begin();
        if(Gdx.input.isTouched()) {

            float xx = conv_x(Gdx.input.getX());
            float yy = conv_y(Gdx.input.getY());
            if(xx>1f) {
                createparticle(((-Gdx.graphics.getWidth()/2)+Gdx.graphics.getHeight()/4)/PIXELS_TO_METERS, ((-Gdx.graphics.getHeight()/2)+Gdx.graphics.getHeight()/4)/PIXELS_TO_METERS);
                body.applyForceToCenter(xx / 300, yy / 300, true);
                }
            //body.setLinearVelocity(xx,yy);
            //body.setActive(false);
        }

        int i=0;
        world.getBodies(bod);
        for(Body b : bod)
        {
        if(b.getType()== BodyDef.BodyType.DynamicBody && b.getUserData().equals("Water"))
          {
                sprite.setPosition((b.getPosition().x * PIXELS_TO_METERS) - sprite.
                                getWidth() / 2,
                        (b.getPosition().y * PIXELS_TO_METERS) - sprite.getHeight() / 2);

                sprites.add(sprite);
                batch.draw(sprites.get(i), sprites.get(i).getX(), sprites.get(i).getY(), sprites.get(i).getOriginX(),
                        sprites.get(i).getOriginY(),
                        sprite.getWidth(), sprite.getHeight(), sprites.get(i).getScaleX(), sprites.get(i).
                                getScaleY(), sprites.get(i).getRotation());
                    /*while(i>150){
                      bod.pop();
                      i--;
                    }*/
                i++;
          }
        }
        batch.end();
        bloom.render();

    }
    int j=0;
    boolean s=false;
    void zombie_render()
    {

        batch.begin();

            if(f==1)
            {
                createZombie1(1);
                //createZombie2(2);
                //createZombie3(3);
                //createZombie4(4);
                //createZombie5(5);
                //createZombie6(6);
                //createZombie7(7);
                f=0;
            }



        world.getBodies(bod);
        for(Body z: bod)
        {
            s=(z.getType()==BodyDef.BodyType.DynamicBody && (z.getUserData().equals("Zombie7") || z.getUserData().equals("Zombie6")
                    || z.getUserData().equals("Zombie3") || z.getUserData().equals("Zombie4")
                    || z.getUserData().equals("Zombie1") || z.getUserData().equals("Zombie2")
                    || z.getUserData().equals("Zombie5")));
            if(z.getType()==BodyDef.BodyType.DynamicBody && z.getPosition().x>=7 && s)
            {

                z.setLinearVelocity(-1,0);
            }
            else if(z.getType()==BodyDef.BodyType.DynamicBody && z.getPosition().x<-5 && s)
            {

                z.setLinearVelocity(90,0);
            }
            else if(z.getType()==BodyDef.BodyType.DynamicBody && z.getLinearVelocity().x==0 && s)
            {

                z.setLinearVelocity(9,0);
            }

            if(z.getType()==BodyDef.BodyType.DynamicBody && z.getUserData().equals("Zombie1"))
            {
                zombie.get(0).setPosition((z.getPosition().x * PIXELS_TO_METERS) - sprite1.
                                getWidth() / 2,
                        (z.getPosition().y * PIXELS_TO_METERS) - sprite1.getHeight() / 2);
                batch.draw(zombie.get(0), zombie.get(0).getX(), zombie.get(0).getY(), zombie.get(0).getOriginX(),
                        zombie.get(0).getOriginY(),
                        zombie.get(0).getWidth(), zombie.get(0).getHeight(), zombie.get(0).getScaleX(), zombie.get(0).
                                getScaleY(), zombie.get(0).getRotation());
                j++;
            }
            if(z.getType()==BodyDef.BodyType.DynamicBody && z.getUserData().equals("Zombie2"))
            {
                zombie.get(1).setPosition((z.getPosition().x * PIXELS_TO_METERS) - sprite1.
                                getWidth() / 2,
                        (z.getPosition().y * PIXELS_TO_METERS) - sprite1.getHeight() / 2);
                batch.draw(zombie.get(1), zombie.get(1).getX(), zombie.get(1).getY(), zombie.get(1).getOriginX(),
                        zombie.get(1).getOriginY(),
                        zombie.get(1).getWidth(), zombie.get(1).getHeight(), zombie.get(1).getScaleX(), zombie.get(1).
                                getScaleY(), zombie.get(1).getRotation());
                j++;
            }
            if(z.getType()==BodyDef.BodyType.DynamicBody && z.getUserData().equals("Zombie3"))
            {
                zombie.get(2).setPosition((z.getPosition().x * PIXELS_TO_METERS) - sprite1.
                                getWidth() / 2,
                        (z.getPosition().y * PIXELS_TO_METERS) - sprite1.getHeight() / 2);
                batch.draw(zombie.get(2), zombie.get(2).getX(), zombie.get(2).getY(), zombie.get(2).getOriginX(),
                        zombie.get(2).getOriginY(),
                        zombie.get(2).getWidth(), zombie.get(2).getHeight(), zombie.get(2).getScaleX(), zombie.get(2).
                                getScaleY(), zombie.get(2).getRotation());
                j++;
            }
            if(z.getType()==BodyDef.BodyType.DynamicBody && z.getUserData().equals("Zombie4"))
            {
                zombie.get(3).setPosition((z.getPosition().x * PIXELS_TO_METERS) - sprite1.
                                getWidth() / 2,
                        (z.getPosition().y * PIXELS_TO_METERS) - sprite1.getHeight() / 2);
                batch.draw(zombie.get(3), zombie.get(3).getX(), zombie.get(3).getY(), zombie.get(3).getOriginX(),
                        zombie.get(3).getOriginY(),
                        zombie.get(3).getWidth(), zombie.get(3).getHeight(), zombie.get(3).getScaleX(), zombie.get(3).
                                getScaleY(), zombie.get(3).getRotation());
                j++;
            }
            if(z.getType()==BodyDef.BodyType.DynamicBody && z.getUserData().equals("Zombie5"))
            {
                zombie.get(4).setPosition((z.getPosition().x * PIXELS_TO_METERS) - sprite1.
                                getWidth() / 2,
                        (z.getPosition().y * PIXELS_TO_METERS) - sprite1.getHeight() / 2);
                batch.draw(zombie.get(4), zombie.get(4).getX(), zombie.get(4).getY(), zombie.get(4).getOriginX(),
                        zombie.get(4).getOriginY(),
                        zombie.get(4).getWidth(), zombie.get(4).getHeight(), zombie.get(4).getScaleX(), zombie.get(4).
                                getScaleY(), zombie.get(4).getRotation());
                j++;
            }
            if(z.getType()==BodyDef.BodyType.DynamicBody && z.getUserData().equals("Zombie6"))
            {
                zombie.get(5).setPosition((z.getPosition().x * PIXELS_TO_METERS) - sprite1.
                                getWidth() / 2,
                        (z.getPosition().y * PIXELS_TO_METERS) - sprite1.getHeight() / 2);
                batch.draw(zombie.get(5), zombie.get(5).getX(), zombie.get(5).getY(), zombie.get(5).getOriginX(),
                        zombie.get(5).getOriginY(),
                        zombie.get(5).getWidth(), zombie.get(5).getHeight(), zombie.get(5).getScaleX(), zombie.get(5).
                                getScaleY(), zombie.get(5).getRotation());
                j++;
            }
            if(z.getType()==BodyDef.BodyType.DynamicBody && z.getUserData().equals("Zombie7"))
            {
                zombie.get(6).setPosition((z.getPosition().x * PIXELS_TO_METERS) - sprite1.
                                getWidth() / 2,
                        (z.getPosition().y * PIXELS_TO_METERS) - sprite1.getHeight() / 2);
                batch.draw(zombie.get(6), zombie.get(6).getX(), zombie.get(6).getY(), zombie.get(6).getOriginX(),
                        zombie.get(6).getOriginY(),
                        zombie.get(6).getWidth(), zombie.get(6).getHeight(), zombie.get(6).getScaleX(), zombie.get(6).
                                getScaleY(), zombie.get(6).getRotation());
                j++;
            }


        }
        batch.end();
    }

    void createEdge()
    {
        float w = Gdx.graphics.getWidth()/PIXELS_TO_METERS;
        // Set the height to just 50 pixels above the bottom of the screen so we can see the edge in the
        // debug renderer
        float h = Gdx.graphics.getHeight()/PIXELS_TO_METERS;

        BodyDef bodyDef1 = new BodyDef();
        bodyDef1.type = BodyDef.BodyType.StaticBody;
        bodyDef1.position.set(0,0);
        FixtureDef fixtureDef1 = new FixtureDef();

        EdgeShape edgeShape1 = new EdgeShape();
        edgeShape1.set(-w/2,-h/2,-w/2,h/2);

        fixtureDef1.shape = edgeShape1;

        bodyEdgeScreen = world.createBody(bodyDef1);
        bodyEdgeScreen.createFixture(fixtureDef1);
        edgeShape1.dispose();

        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyDef.BodyType.StaticBody;
        bodyDef2.position.set(0,0);
        FixtureDef fixtureDef2 = new FixtureDef();

        EdgeShape edgeShape2 = new EdgeShape();
        edgeShape2.set(-w/2,-h/2,w/2,-h/2);

        fixtureDef2.shape = edgeShape2;

        bodyEdgeScreen = world.createBody(bodyDef2);
        bodyEdgeScreen.createFixture(fixtureDef2);
        edgeShape2.dispose();

        BodyDef bodyDef3 = new BodyDef();
        bodyDef3.type = BodyDef.BodyType.StaticBody;
        bodyDef3.position.set(0,0);
        FixtureDef fixtureDef3 = new FixtureDef();

        EdgeShape edgeShape3 = new EdgeShape();
        edgeShape3.set(w/2,-h/2,w/2,h/2);
        fixtureDef3.shape = edgeShape3;

        bodyEdgeScreen = world.createBody(bodyDef3);
        bodyEdgeScreen.createFixture(fixtureDef3);
        edgeShape3.dispose();

        BodyDef bodyDef4 = new BodyDef();
        bodyDef4.type = BodyDef.BodyType.StaticBody;
        bodyDef4.position.set(0,0);
        FixtureDef fixtureDef4 = new FixtureDef();

        EdgeShape edgeShape4 = new EdgeShape();
        edgeShape4.set(-w/2,h/2,w/2,h/2);

        fixtureDef4.shape = edgeShape4;

        bodyEdgeScreen = world.createBody(bodyDef4);
        bodyEdgeScreen.createFixture(fixtureDef4);
        edgeShape4.dispose();

        FixtureDef fixtureDef5 = new FixtureDef();

        EdgeShape edgeShape5 = new EdgeShape();
        edgeShape5.set(-w/2,-h/3,w/2,-h/3);

        fixtureDef5.shape = edgeShape5;

        bodyEdgeScreen = world.createBody(bodyDef4);
        bodyEdgeScreen.createFixture(fixtureDef5);
        edgeShape5.dispose();
    }

    @Override
    public void dispose() {
        img.dispose();
        img1.dispose();
        img2.dispose();
        world.dispose();

    }
    @Override
    public boolean keyDown(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        return true;
    }
     public boolean createparticle(float X, float Y) {
         //sprite.setPosition(sprite.getWidth()/2,sprite.getHeight()/2);
         BodyDef bodyDef = new BodyDef();
         bodyDef.type = BodyDef.BodyType.DynamicBody;
         bodyDef.position.set(X, Y);
         body = world.createBody(bodyDef);
         CircleShape shape = new CircleShape();
         shape.setRadius(sprite.getWidth() / 200);

         FixtureDef fixtureDef = new FixtureDef();
         fixtureDef.shape = shape;
         fixtureDef.density = 0.1f;
         fixtureDef.restitution = 0.5f;
         body.createFixture(fixtureDef);
         body.setUserData("Water");
         //body.setBullet(true);
         shape.dispose();
         //body.setActive(true);
         return true;
     }
    public void createZombie1(int n)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0, 0);
        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(sprite1.getWidth() / 200);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0.5f;
        body.createFixture(fixtureDef);
        body.setUserData("Zombie1");
        shape.dispose();
    }
    public void createZombie2(int n)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(1, 0);
        //body.setLinearVelocity(n,0);
        body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(sprite1.getWidth() / 200);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0.5f;
        body.createFixture(fixtureDef);
        body.setUserData("Zombie2");
        shape.dispose();
    }
    public void createZombie3(int n)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(2, 0);
        //body.setLinearVelocity(n,0);
        body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(sprite1.getWidth() / 200);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0.5f;
        body.createFixture(fixtureDef);
        body.setUserData("Zombie3");
        shape.dispose();
    }
    public void createZombie4(int n)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(3, 0);
        //body.setLinearVelocity(n,0);
        body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(sprite1.getWidth() / 200);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0.5f;
        body.createFixture(fixtureDef);
        body.setUserData("Zombie4");
        shape.dispose();
    }
    public void createZombie5(int n)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(4, 0);
        //body.setLinearVelocity(n,0);
        body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(sprite1.getWidth() / 200);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0.5f;
        body.createFixture(fixtureDef);
        body.setUserData("Zombie5");
        shape.dispose();
    }
    public void createZombie6(int n)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(5, 0);
        //body.setLinearVelocity(n,0);
        body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(sprite1.getWidth() / 200);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0.5f;
        body.createFixture(fixtureDef);
        body.setUserData("Zombie6");
        shape.dispose();
    }
    public void createZombie7(int n)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(6, 0);
        //body.setLinearVelocity(1,0);
        body = world.createBody(bodyDef);
        CircleShape shape = new CircleShape();
        shape.setRadius(sprite1.getWidth() / 200);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0.5f;
        body.createFixture(fixtureDef);
        body.setUserData("Zombie7");
        shape.dispose();
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }
}
