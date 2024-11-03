precision mediump float;

uniform highp float u_time;  // Use high precision for smoother animation
uniform vec2 u_resolution;


void main() {
    // Calculate aspect ratio and normalize `uv` coordinates
    float aspectRatio = u_resolution.x / u_resolution.y;
    vec2 uv = gl_FragCoord.xy / u_resolution;
    uv.x *= aspectRatio;

    // Move lights vertically by modifying only the `y` coordinate
    uv.y += mod(u_time * 0.1, 1.0) - 0.65;

    float numLights = 12.0;
    float lightSpeed = 0.2;
    vec3 color = vec3(0.0);

    for (float i = 0.0; i < numLights; i++) {
        float offset = i / numLights;

        // Generate a random horizontal offset for each light based on its index
  // Range from -0.25 to +0.25
        float lightIntensity = 1.0 - abs(mod(uv.y + offset + u_time * lightSpeed, 1.0) - 0.5) * 2.0;

        // Transition colors between red and blue for different lights
        vec3 lightColor = mix(vec3(1.0, 0.2, 0.2), vec3(0.2, 0.2, 1.0), offset);

        // Calculate the distance with the random horizontal offset for each light
        float dist = length(uv - vec2(mod(offset * 2.0 + u_time * 0.2 , 1.0), mod(offset + u_time * lightSpeed, 1.0))) * 0.4;
        float softness = 0.15;  // Control edge softness

        // Apply smoothstep with softened edges
        color += lightColor * smoothstep(softness, 0.0, dist) * lightIntensity;
    }

    gl_FragColor = vec4(color, 1.0);
}