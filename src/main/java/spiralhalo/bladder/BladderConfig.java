package spiralhalo.bladder;

import spiralhalo.bladder.util.SizeUtil;

public class BladderConfig {
    public static boolean hudRightToLeft = Defaults.hudRightToLeft;
    public static float hudOffsetX = Defaults.hudOffsetX;
    public static float hudOffsetY = Defaults.hudOffsetY;

    private static class Defaults {
        private static boolean hudRightToLeft = true;
        private static float hudOffsetX = SizeUtil.HOTBAR_HALFWIDTH;
        private static float hudOffsetY = -SizeUtil.HOTBAR_HUNGERBAR_HEIGHT;
    }
}
