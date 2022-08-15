#version 330

in vec3 textureCoords;
out vec4 out_Color;

uniform samplerCube in_skybox;

void main() {
    gl_FragColor = texture(in_skybox, textureCoords);
}
