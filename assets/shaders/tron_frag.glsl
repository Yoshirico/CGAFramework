#version 330 core


uniform vec3 pointLightColor;
uniform vec3 point1Color;
uniform vec3 point2Color;
uniform vec3 point3Color;
uniform vec3 spotLightColor;
uniform float spotLightInnerCone;
uniform float spotLightOuterCone;
uniform float pointLightConstantAttenuation;
uniform float pointLightLinearAttenuation;
uniform float pointLightQuadraticAttenuation;

uniform float point1ConstantAttenuation;
uniform float point1LinearAttenuation;
uniform float point1QuadraticAttenuation;

uniform float point2ConstantAttenuation;
uniform float point2LinearAttenuation;
uniform float point2QuadraticAttenuation;

uniform float point3ConstantAttenuation;
uniform float point3LinearAttenuation;
uniform float point3QuadraticAttenuation;

uniform float spotLightConstantAttenuation;
uniform float spotLightLinearAttenuation;
uniform float spotLightQuadraticAttenuation;


uniform float shininess;
uniform sampler2D emitTex;
uniform vec3 emitColor;
uniform sampler2D diffTex;
uniform sampler2D specTex;


out vec4 color;
in vec3 toLight;
in vec3 toLight1;
in vec3 toLight2;
in vec3 toLight3;
in vec3 halfwayDir;
in vec3 halfwayDir1;
in vec3 halfwayDir2;
in vec3 halfwayDir3;
in vec3 toSpotLight;
in vec3 toCamera;
in vec3 viewSpotLightDirection;


//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec2 textureCoord;
    vec3 normal;
} vertexData;


void main(){

    //alles normalisieren
    vec3 normalizedToLight = normalize (toLight);
    vec3 normalizedToLight1 = normalize (toLight1);
    vec3 normalizedToLight2 = normalize (toLight2);
    vec3 normalizedToLight3 = normalize (toLight3);
    vec3 normalizedToSpotLight = normalize (toSpotLight);
    vec3 normalizedToCamera = normalize (toCamera);
    vec3 normalizedNormal = normalize (vertexData.normal);
    vec3 normalizedSpotLightDirection = normalize(viewSpotLightDirection);

    // returns value in linear space
    float gamma = 2.2;
    vec3 diffuseColor = pow(texture(diffTex, vertexData.textureCoord).rgb,vec3(gamma));
    vec3 specColor = pow(texture(specTex, vertexData.textureCoord).rgb,vec3(gamma));
    vec3 emiColor = pow(texture(emitTex, vertexData.textureCoord).rgb,vec3(gamma));

    //halfvector
    vec3 halfwayDir = normalize(normalizedToLight + normalizedToCamera);
    vec3 halfwayDir1 = normalize(normalizedToLight1 + normalizedToCamera);
    vec3 halfwayDir2 = normalize(normalizedToLight2 + normalizedToCamera);
    vec3 halfwayDir3 = normalize(normalizedToLight3 + normalizedToCamera);

    // bodenlichtberechnung
    float distanceToLight = length(toLight);
    float pointAttenuation = 1f / (pointLightConstantAttenuation + pointLightLinearAttenuation * distanceToLight + pointLightQuadraticAttenuation * (distanceToLight*distanceToLight));//Md
    float brightnessDiff = max(0.0, dot(normalizedNormal, normalizedToLight));//cosA
    vec3 finalDiff =  pointAttenuation * brightnessDiff * pointLightColor * diffuseColor;//D
    vec3 reflectedToLight = reflect(-normalizedToLight, normalizedNormal);//Ms
    float brightnessSpecular = max(0.0, dot(normalizedNormal, halfwayDir));//cosB
    vec3 finalSpec = pointAttenuation * pow(brightnessSpecular, shininess) * specColor * pointLightColor;//C
    //licht1
    float distanceToLight1 = length(toLight1);
    float pointAttenuation1 = 1f / (point1ConstantAttenuation + point1LinearAttenuation * distanceToLight1 + point1QuadraticAttenuation * (distanceToLight1*distanceToLight1));
    float brightnessDiff1 = max(0.0, dot(normalizedNormal, normalizedToLight1));
    vec3 finalDiff1 =  pointAttenuation1 * point1Color * brightnessDiff * diffuseColor;
    vec3 reflectedToLight1 = reflect(-normalizedToLight1, normalizedNormal);
    float brightnessSpecular1 = max(0.0, dot(normalizedNormal, halfwayDir1));
    vec3 finalSpec1 = pointAttenuation1 * pow(brightnessSpecular1, shininess) * specColor * point1Color;
    //licht2
    float distanceToLight2 = length(toLight2);
    float pointAttenuation2 = 1f / (point2ConstantAttenuation + point2LinearAttenuation * distanceToLight2 + point2QuadraticAttenuation * (distanceToLight2*distanceToLight2));
    float brightnessDiff2 = max(0.0, dot(normalizedNormal, normalizedToLight2));
    vec3 finalDiff2 =  pointAttenuation2 * point1Color * brightnessDiff * diffuseColor;
    vec3 reflectedToLight2 = reflect(-normalizedToLight2, normalizedNormal);
    float brightnessSpecular2 = max(0.0, dot(normalizedNormal, halfwayDir2));
    vec3 finalSpec2 = pointAttenuation2 * pow(brightnessSpecular2, shininess) * specColor * point2Color;
    //licht3
    float distanceToLight3 = length(toLight3);
    float pointAttenuation3 = 1f / (point3ConstantAttenuation + point3LinearAttenuation * distanceToLight3 + point3QuadraticAttenuation * (distanceToLight3*distanceToLight3));
    float brightnessDiff3 = max(0.0, dot(normalizedNormal, normalizedToLight3));
    vec3 finalDiff3 =  pointAttenuation3 * point1Color * brightnessDiff * diffuseColor;
    vec3 reflectedToLight3 = reflect(-normalizedToLight3, normalizedNormal);
    float brightnessSpecular3 = max(0.0, dot(normalizedNormal, halfwayDir3));
    vec3 finalSpec3 = pointAttenuation3 * pow(brightnessSpecular3, shininess) * specColor * point3Color;


    // scheinwerferberechnung
    float distanceToSpot = length(toSpotLight);
    float spotAttenuation = 1f / (spotLightConstantAttenuation + spotLightLinearAttenuation * distanceToSpot + spotLightQuadraticAttenuation * (distanceToSpot*distanceToSpot));
    float theta = dot(normalizedToSpotLight, normalize(-normalizedSpotLightDirection)); // Kreis mit horizontalen strich
    float epsilon = spotLightInnerCone - spotLightOuterCone; // Kreis mit vertikalem Strich (innen)
    float intensity = clamp((theta-spotLightOuterCone)/epsilon, 0f, 1f);
    float brightnessSpotDiff = max(dot(normalizedNormal, normalizedToSpotLight), 0f);
    vec3 finalSpotDiff = spotAttenuation * brightnessSpotDiff * spotLightColor * intensity * texture(diffTex, vertexData.textureCoord).rgb; // in result

    vec3 reflectedToSpotLight = reflect(-normalizedToSpotLight, normalizedNormal);
    float brightnessSpotSpecular = max(0.0, dot(reflectedToSpotLight, normalizedToCamera));
    vec3 finalSpotSpec = spotAttenuation * pow(brightnessSpotSpecular, shininess) * texture(specTex, vertexData.textureCoord).rgb * spotLightColor;

    vec3 ambient = 0.1 * pointLightColor * pointAttenuation;
    vec3 ambient1 = 0.1 * point1Color * pointAttenuation1;
    vec3 ambient2 = 0.1 * point2Color * pointAttenuation2;
    vec3 ambient3 = 0.1 * point3Color * pointAttenuation3;

    vec3 result = emitColor * emiColor + (ambient * finalDiff + finalSpec + ambient1 * finalDiff1 + finalSpec1 + ambient2 * finalDiff2 + finalSpec2  + ambient3 * finalDiff3 + finalSpec3 + finalSpotDiff);

    // returns value in gamma / sRGB space
    vec3 gamma1 = pow(result.rgb,vec3(1.0/gamma));
    color = vec4 (gamma1,1.0);

}