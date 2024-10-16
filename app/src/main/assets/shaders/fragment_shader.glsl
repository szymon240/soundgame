precision highp float;
uniform sampler2D u_Texture;
varying vec2 iTexCoord0;

void main(void) {
    gl_FragColor = texture2D(u_Texture, iTexCoord0);
}