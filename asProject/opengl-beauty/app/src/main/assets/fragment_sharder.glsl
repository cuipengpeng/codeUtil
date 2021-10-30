#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform samplerExternalOES texture;
varying vec2 aCoordinate;
uniform mat4 uColorMatrix;
void main () {
    //vec4 color = texture2D(texture, aCoordinate).rgba;
    vec4 color = uColorMatrix * texture2D(texture, aCoordinate).rgba;
    gl_FragColor = color;
}