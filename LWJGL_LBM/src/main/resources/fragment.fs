//https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/
#version 330

in  vec3 exColour;
out vec4 fragColor;

void main()
{
	fragColor = vec4(exColour, 1.0);
}