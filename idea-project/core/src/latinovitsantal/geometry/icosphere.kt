package latinovitsantal.geometry

import com.badlogic.gdx.math.Vector3

internal val t = (1f + Math.sqrt(5.0).toFloat()) / 2f
internal const val o = 0f
internal const val i = 1f
internal val vertices = listOf(
	Vector3(-i, t, o),
	Vector3(i, t, o),
	Vector3(-i, -t, o),
	Vector3(i, -t, o),
	Vector3(o, -i, t),
	Vector3(o, i, t),
	Vector3(o, -i, -t),
	Vector3(o, i, -t),
	Vector3(t, o, -i),
	Vector3(t, o, i),
	Vector3(-t, o, -i),
	Vector3(-t, o, i)
).apply { forEach { it.length = 1f } }
internal fun tri(a: Int, b: Int, c: Int) = Triangle(vertices[a], vertices[b], vertices[c])
internal val icosahedron = listOf(
	tri(0, 11, 5),
	tri(0, 5, 1),
	tri(0, 1, 7),
	tri(0, 7, 10),
	tri(0, 10, 11),
	tri(1, 5, 9),
	tri(5, 11, 4),
	tri(11, 10, 2),
	tri(10, 7, 6),
	tri(7, 1, 8),
	tri(3, 9, 4),
	tri(3, 4, 2),
	tri(3, 2, 6),
	tri(3, 6, 8),
	tri(3, 8, 9),
	tri(4, 9, 5),
	tri(2, 4, 11),
	tri(6, 2, 10),
	tri(8, 6, 7),
	tri(9, 8, 1)
	)

fun icoSphere(d: Int = 0): List<Triangle> {
	var result = icosahedron
	for (i in 1..d)
		result = result.flatMap { it.division() }
	return result
}

internal fun Triangle.division(): List<Triangle> {
	val na = ((b + c) / 2f).apply { length = 1f }
	val nb = ((a + c) / 2f).apply { length = 1f }
	val nc = ((a + b) / 2f).apply { length = 1f }
	return listOf(
		Triangle(na, nb, nc),
		Triangle(a, nc, nb),
		Triangle(b, na, nc),
		Triangle(c, nb, na)
	)
}

operator fun Vector3.plus(v: Vector3) = Vector3(x+v.x, y+v.y, z+v.z)
operator fun Vector3.plusAssign(v: Vector3) { x+=v.x; y+=v.y; z+=v.z }
operator fun Vector3.times(f: Float) = Vector3(x*f, y*f, z*f)
operator fun Vector3.timesAssign(f: Float) { x*=f; y*=f; z*=f }
operator fun Vector3.div(f: Float) = Vector3(x/f, y/f, z/f)
operator fun Vector3.divAssign(f: Float) { x/=f; y/=f; z/=f }
var Vector3.length: Float get() = len()
	set(l: Float) { this *= (l / length) }