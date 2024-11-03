precision mediump float;

uniform float u_time;
uniform vec2 u_resolution;

void main() {
    // Calculate aspect ratio
    float aspectRatio = u_resolution.x / u_resolution.y;

    // Normalize `uv` coordinates with aspect ratio for circular lights
    vec2 uv = gl_FragCoord.xy / u_resolution;
    uv.x *= aspectRatio;
    uv.y += mod(u_time * 0.1, 1.0);  // Shift uv.y for upward movement

    float numLights = 5.0;
    float lightSpeed = 0.2;
    vec3 color = vec3(0.0);

    for (float i = 0.0; i < numLights; i++) {
        float offset = i / numLights;
        float lightIntensity = 1.0 - abs(mod(uv.y + offset + u_time * lightSpeed, 1.0) - 0.5) * 2.0;

        // Transition colors between red and blue
        vec3 lightColor = mix(vec3(1.0, 0.2, 0.2), vec3(0.2, 0.2, 1.0), offset);

        // Calculate the distance for circular lights
        float dist = length(uv - vec2(mod(offset * 2.0 + u_time * 0.2, 1.0), mod(offset + u_time * lightSpeed, 1.0)));

        // Use smoothstep for a smooth light boundary
        color += lightColor * smoothstep(0.1, 0.0, dist) * lightIntensity;
    }

    gl_FragColor = vec4(color, 1.0);
}