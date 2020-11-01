attribute vec4 aVertPosition;
attribute vec4 aTexPosition;
uniform mat4 uTexMatrix;
uniform mat4 uMVPMatrix;
varying vec2 aCoordinate;
void main () {
    aCoordinate = (uTexMatrix * aTexPosition).xy;
    //gl_Position = aVertPosition;
     gl_Position = uMVPMatrix * aVertPosition;
}