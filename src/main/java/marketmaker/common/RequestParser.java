package marketmaker.common;

public interface RequestParser {
    Request parseFromString(String instruction) throws Exception;
}
