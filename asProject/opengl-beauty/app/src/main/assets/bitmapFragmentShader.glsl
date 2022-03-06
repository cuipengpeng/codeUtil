precision mediump float;
varying vec2 vTexCoord;
uniform sampler2D sTexture;
uniform mat4 uColorMatrix;
void main() {
    gl_FragColor= uColorMatrix * texture2D(sTexture, vTexCoord).rgba;
    //gl_FragColor = texture2D(sTexture, vTexCoord);
}