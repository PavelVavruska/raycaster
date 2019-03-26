package raycaster;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RaycasterTest {

    /**
     * Makes sure the {@link Raycaster} application is runnable
     */
    @Test
    @DisplayName("Raycaster is runnable")
    public void raycaster() {
        Raycaster.main(new String[]{});
    }

}