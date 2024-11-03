precision highp float;
uniform sampler2D u_Texture;
varying vec2 iTexCoord0;

void main(void) {
    vec4 color = texture2D(u_Texture, iTexCoord0);
    gl_FragColor = color;
}