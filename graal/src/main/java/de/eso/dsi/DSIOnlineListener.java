package de.eso.dsi;

import de.eso.api.DSIListener;

public interface DSIOnlineListener extends DSIListener {
  void updateRole(int role, int validFlag);
}
