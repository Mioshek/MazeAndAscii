package window;

import imgui.ImGui;
import imgui.app.Color;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.Objects;

/**
 * This is a modified version of imgui.app.Window <br>
 * For docs refer to: {@link imgui.app.Window}
 */
public abstract class WindowBase{
    public final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    public final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private String glslVersion = null;
    public static long windowPtr;
    public final Color colorBg = new Color(.5f, .5f, .5f, 1);
    private boolean initialized = false, disposed = false, running = false;

    public void init(){
        if (initialized){
            throw new RuntimeException("This window has already been initialized");
        }
        initialized = true;
        final Config config = new Config();
        initWindow(config);
        initImGui();
        imGuiGlfw.init(windowPtr, true);
        imGuiGl3.init(glslVersion);
    }

    public void dispose(){
        if (disposed){
            return;
        }
        disposed = true;
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        disposeImGui();
        disposeWindow();
    }

    /**
     * Method to create and initialize GLFW window.
     *
     * @param config configuration object with basic window information
     */
    protected void initWindow(final Config config){
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        decideGlGlslVersions();
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, config.getResizable() ? 1 : 0);
        GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, config.getDecorated() ? 1 : 0);
        //hidden to be set up, shown later
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        windowPtr = GLFW.glfwCreateWindow(config.getWidth(), config.getHeight(), config.getTitle(), MemoryUtil.NULL, MemoryUtil.NULL);

        if (windowPtr == MemoryUtil.NULL){
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = MemoryStack.stackPush()){
            final IntBuffer pWidth = stack.mallocInt(1); // int*
            final IntBuffer pHeight = stack.mallocInt(1); // int*

            GLFW.glfwGetWindowSize(windowPtr, pWidth, pHeight);
            final GLFWVidMode vidMode = Objects.requireNonNull(GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor()));
            GLFW.glfwSetWindowPos(windowPtr, (vidMode.width() - pWidth.get(0)) / 2, (vidMode.height() - pHeight.get(0)) / 2);
        }

        GLFW.glfwMakeContextCurrent(windowPtr);

        GL.createCapabilities();

        GLFW.glfwSwapInterval(GLFW.GLFW_TRUE);

        if (config.getFullscreen()){
            GLFW.glfwMaximizeWindow(windowPtr);
        }else{
            GLFW.glfwShowWindow(windowPtr);
        }

        clearBuffer();
        renderBuffer();

        GLFW.glfwSetWindowSizeCallback(windowPtr, new GLFWWindowSizeCallback(){
            @Override
            public void invoke(final long window, final int width, final int height){
                runFrame();
            }
        });
    }

    private void decideGlGlslVersions(){
        final boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        if (isMac){
            glslVersion = "#version 150";
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);  // 3.2+ only
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);          // Required on Mac
        }else{
            glslVersion = "#version 130";
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);
        }
    }

    //is run in init()
    protected abstract void initImGui();

    public void run(){
        if (running){
            return;
        }
        running = true;
        while (!GLFW.glfwWindowShouldClose(windowPtr)){
            runFrame();
        }
        running = false;
    }

    protected void runFrame(){
        startFrame();
        process();
        endFrame();
    }

    public abstract void process();

    private void clearBuffer(){
        GL32.glClearColor(colorBg.getRed(), colorBg.getGreen(), colorBg.getBlue(), colorBg.getAlpha());
        GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);
    }


    protected void startFrame(){
        clearBuffer();
        imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    /**
     * Method called in the end of the main cycle.
     * It renders ImGui and swaps GLFW buffers to show an updated frame.
     */
    protected void endFrame(){
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)){
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }

        renderBuffer();
    }

    private void renderBuffer(){
        GLFW.glfwSwapBuffers(windowPtr);
        GLFW.glfwPollEvents();
    }

    protected void disposeImGui(){
        ImGui.destroyContext();
    }

    protected void disposeWindow(){
        Callbacks.glfwFreeCallbacks(windowPtr);
        GLFW.glfwDestroyWindow(windowPtr);
        GLFW.glfwTerminate();
        Objects.requireNonNull(GLFW.glfwSetErrorCallback(null)).free();
    }

    public final long getWindowPtr(){
        return windowPtr;
    }

    public final Color getColorBg(){
        return colorBg;
    }
}
