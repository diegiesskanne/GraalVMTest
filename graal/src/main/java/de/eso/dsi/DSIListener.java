package de.eso.dsi;

public interface DSIListener {
  void asyncException(int errorCode, String errorMsg, int requestType);
}
