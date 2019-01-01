package de.eso.api;

public interface DSIListener {
  void asyncException(int errorCode, String errorMsg, int requestType);
}
