# [-> Youtube Video](https://www.youtube.com/watch?v=uYqJzrDB6gE)

This is my implementation of Mahjong Solitaire in Java using the LibGDX library. LibGDX is "a cross-platform Java game development framework based on OpenGL (ES) that works on Windows, Linux, macOS, Android, your browser and iOS". It allows you to easily load models and textures and present them on the screen with minimal code and minimal OpenGL knowledge. I implemented the tiles as a hash map, where the keys are integer tile coordinates (column, row, layer). The column and row tile coordinates are in half-tile units so that tiles can be partially overlapping each other. LibGDX made it easy to do tile selection; I simply get a "pick ray" from the camera object and let it intersect with the plane associated with each tile layer and convert the coordinates to int. The hash map allows me to locate all 26 potential neighboring tiles to quickly check if a tile is removable. 

I have some criticisms about the LibGDX API and it's wiki/documentation which I will keep to myself, although I will say that using the particle system/controller was highly unintuitive and annoying. Overall though, I think LibGDX is a great starting point if you want to get 3D graphics running in Java quickly.

All the models and textures (except the wood texture) were created by me in Blender and Paint.net. The wood texture is a procedurally generated and seamless Substance Designer material from ambientcg.com.

Controls:
- LMB - Select tiles
- RMB - Rotate camera
- WASD - Move camera
- Mouse wheel/button - Zoom camera
- Left/right - Change puzzle layout
- F - Go fullscreen
- Escape - Exit application
