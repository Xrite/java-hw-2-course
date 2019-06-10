import org.jetbrains.annotations.NotNull;

/** Interface for objects that can be rendered */
public interface Renderable {
    /** Renders object using given renderer */
    void render(@NotNull Renderer renderer);
}
