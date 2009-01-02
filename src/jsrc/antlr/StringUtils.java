package antlr;

public class StringUtils {

    private StringUtils() {
    }

    /**
     * General-purpose utility function for removing characters from back of
     * string
     *
     * @param s The string to process
     * @param c The character to remove
     * @return The resulting string
     */
    static public String stripBack(String s, char c) {
        while (0 < s.length() && s.charAt(s.length() - 1) == c) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    /**
     * General-purpose utility function for removing characters from back of
     * string
     *
     * @param s      The string to process
     * @param remove A string containing the set of characters to remove
     * @return The resulting string
     */
    static public String stripBack(String s, String remove) {
        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < remove.length(); i++) {
                char c = remove.charAt(i);
                while (0 < s.length() && s.charAt(s.length() - 1) == c) {
                    changed = true;
                    s = s.substring(0, s.length() - 1);
                }
            }
        } while (changed);
        return s;
    }

    /**
     * General-purpose utility function for removing characters from front of
     * string
     *
     * @param s The string to process
     * @param c The character to remove
     * @return The resulting string
     */
    static public String stripFront(String s, char c) {
        while (0 < s.length() && s.charAt(0) == c) {
            s = s.substring(1);
        }
        return s;
    }

    /**
     * General-purpose utility function for removing characters from front of
     * string
     *
     * @param s      The string to process
     * @param remove A string containing the set of characters to remove
     * @return The resulting string
     */
    static public String stripFront(String s, String remove) {
        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < remove.length(); i++) {
                char c = remove.charAt(i);
                while (0 < s.length() && s.charAt(0) == c) {
                    changed = true;
                    s = s.substring(1);
                }
            }
        } while (changed);
        return s;
    }

    /**
     * General-purpose utility function for removing characters from the front
     * and back of string
     *
     * @param s    The string to process
     * @param head exact string to strip from head
     * @param tail exact string to strip from tail
     * @return The resulting string
     */
    static public String stripFrontBack(String src, String head, String tail) {
        int h = src.indexOf(head);
        int t = src.lastIndexOf(tail);
        if (-1 == h || -1 == t) {
            return src;
        }
        return src.substring(h + 1, t);
    }
}
