package de.eso.dsi;

public interface DSIWLANListener extends DSIListener {

  void updateRole(int role, int validFlag);

  void updateRFActive(int rfActive, int validFlag);

  void responseSetWpsKeypadPin(int result);
}
