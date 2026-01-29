package com.acme.iiq.domain.caseFile;

import com.acme.iiq.domain.event.LifecycleEventType;

public enum CaseType {

    JOINER,
    MOVER,
    LEAVER;

    public static CaseType from(LifecycleEventType eventType){
      return switch (eventType){
          case MOVER -> MOVER;
          case JOINER -> JOINER;
          case LEAVER -> LEAVER;
      };
    }
}
