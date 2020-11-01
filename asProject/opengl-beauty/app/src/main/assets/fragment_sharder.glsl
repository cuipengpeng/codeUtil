#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES texture;
varying vec2 aCoordinate;
void main () {
    vec4 color = texture2D(texture, aCoordinate);
    gl_FragColor = color;
}