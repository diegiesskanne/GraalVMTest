package de.eso.graalvm;

import io.reactivex.Observable;

public interface MyService {
  Observable<Long> getTime();
}
