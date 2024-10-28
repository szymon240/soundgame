precision mediump float;

uniform float u_time;
uniform vec2 u_resolution;

void main() {
    vec2 uv = gl_FragCoord.xy / u_resolution;
    uv.y += mod(u_time * 0.2, 2.0);

    float numLights = 5.0;
    float lightSpeed = 0.5;
    vec3 color = vec3(0.0);

    for (float i = 0.0; i < numLights; i++) {
        float offset = i / numLights;
        float lightIntensity = 1.0 - abs(mod(uv.y + offset + u_time * lightSpeed, 1.0) - 0.5) * 2.0;
        vec3 lightColor = mix(vec3(1.0, 0.2, 0.2), vec3(0.2, 0.2, 1.0), offset);

        float dist = length(uv - vec2(mod(offset * 2.0 + u_time * 0.2, 1.0), mod(offset + u_time * lightSpeed, 1.0)));

        color += lightColor * smoothstep(0.1, 0.0, dist) * lightIntensity;
    }

    gl_FragColor = vec4(color, 1.0);
}