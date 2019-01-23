package icospheredemo

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT
import com.badlogic.gdx.graphics.GL20.GL_DEPTH_BUFFER_BIT
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.VertexAttributes.Usage.Position
import com.badlogic.gdx.graphics.VertexAttributes.Usage.Normal
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.utils.TimeUtils
import latinovitsantal.geometry.icoSphere
import java.util.*

val r = Random()
val randomMat: Material get() =
	Material(ColorAttribute.createDiffuse(r.nextFloat(), r.nextFloat(), r.nextFloat(), 1f))

class SphereDemo : ApplicationAdapter() {

	lateinit var camera: PerspectiveCamera
	lateinit var model: Model
	private val instances = mutableListOf<ModelInstance>()
	var instanceIndex = 0
	var lastPressedTime = currentTime
	lateinit var batch: ModelBatch
	lateinit var fontBatch: SpriteBatch
	lateinit var environment: Environment
	lateinit var camController: CameraInputController
	private val usage = usageOf(Position, Normal)
	lateinit var font: BitmapFont

	override fun create() {
		camera = PerspectiveCamera(75f, Width.toFloat(), Height.toFloat()).apply {
			position.set(0f, 0f, 10f)
			lookAt(0f, 0f, 0f)
			near = 0.1f
			far = 300f
			update()
		}


		for(j in 0..3) {
			val faces = icoSphere(j) //torus(1f, 0.2f, 20, 50)
			model = ModelBuilder().invoke {
				for (i in faces.indices) {
					val part = part("id$i", GL20.GL_TRIANGLES, usage, randomMat)
					part.triangle(faces[i].a, faces[i].b, faces[i].c)
				}
			}
			instances += ModelInstance(model)
		}

		batch = ModelBatch()
		environment = Environment().apply {
			set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
			add(DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f))
		}
		camController = CameraInputController(camera)
		Gdx.input.inputProcessor = camController
		font = BitmapFont()
		fontBatch = SpriteBatch()

	}

	override fun render() {
		Gdx.gl.glViewport(0, 0, Width, Height)
		Gdx.gl.glClear(or(GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT))
		batch(camera) {
			render(instances[instanceIndex], environment)
		}
		fontBatch {
			font.draw(this, "Press Enter to toggle!", 5f, 20f)
		}

		if(Input.Keys.ENTER.isPressed && currentTime - lastPressedTime > 1000000000) {
			instanceIndex++
			instanceIndex %= instances.size
			lastPressedTime = currentTime
		}
	}

	override fun dispose() {
		model.dispose()
		batch.dispose()
	}

}


val Int.isPressed: Boolean get() = Gdx.input.isKeyPressed(this)
val currentTime: Long get() = TimeUtils.nanoTime()
operator fun SpriteBatch.invoke(func: SpriteBatch.() -> Unit): Unit {
	begin()
	func()
	end()
}
fun usageOf(vararg usages: Int) = or(*usages).toLong()
fun or(vararg args: Int) = args.fold(0) { res, it -> res or it }
val Width: Int get() = Gdx.graphics.width
val Height: Int get() = Gdx.graphics.height
operator fun ModelBuilder.invoke(func: ModelBuilder.() -> Unit): Model {
	begin()
	func()
	return end()
}
operator fun ModelBatch.invoke(cam: Camera, func: ModelBatch.() -> Unit): Unit {
	begin(cam)
	func()
	end()
}
