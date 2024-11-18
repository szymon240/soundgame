uniform mat4 uMVPMatrix;
attribute vec4 vPosition;

attribute vec2 texCoord0;
varying vec2 iTexCoord0;

void main(void) {
    iTexCoord0 = texCoord0;
    gl_Position = uMVPMatrix * vPosition;
}