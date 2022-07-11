#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoord;
layout(location = 2) in vec3 normal;

//uniforms
// translation object to world
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

uniform vec2 tcMultiplier;

//Light
uniform vec3 pointLightPosition;
uniform vec3 point1Position;
uniform vec3 point2Position;
uniform vec3 point3Position;
uniform vec3 spotLightPosition;
uniform vec3 spotLightDirection;


out struct VertexData
{
    vec3 position;
    vec2 textureCoord;
    vec3 normal;
} vertexData;

out vec3 toLight;
out vec3 toLight1;
out vec3 toLight2;
out vec3 toLight3;

out vec3 halfwayDir;

out vec3 toSpotLight;
out vec3 toCamera;
out mat4 passInverseTransposeViewMatrix;
out vec3 viewSpotLightDirection;


void main(){
    vec4 pos = vec4(position, 1.0f);
    vec4 worldPosition = model_matrix * pos;
    vec4 positionInCameraSpace = view_matrix * worldPosition;
    mat4 inverseTransposeViewMatrix = inverse(transpose(view_matrix));

    gl_Position = projection_matrix * positionInCameraSpace;

    vertexData.position = worldPosition.xyz;
    vertexData.textureCoord = tcMultiplier * textureCoord;

    vertexData.normal = (inverseTransposeViewMatrix * model_matrix * vec4(normal, 0f)).xyz;

    toLight = (view_matrix * vec4(pointLightPosition, 1f)).xyz - positionInCameraSpace.xyz;
    toLight1 = (view_matrix * vec4(point1Position, 1f)).xyz - positionInCameraSpace.xyz;
    toLight2 = (view_matrix * vec4(point2Position, 1f)).xyz - positionInCameraSpace.xyz;
    toLight3 = (view_matrix * vec4(point3Position, 1f)).xyz - positionInCameraSpace.xyz;
    toSpotLight = (view_matrix * vec4(spotLightPosition, 1f)).xyz - positionInCameraSpace.xyz;
    toCamera = -positionInCameraSpace.xyz;
    viewSpotLightDirection = (view_matrix * vec4(spotLightDirection, 0f)).xyz;

}