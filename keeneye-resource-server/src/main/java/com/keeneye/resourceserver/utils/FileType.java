package com.keeneye.resourceserver.utils;

import lombok.Getter;

@Getter
public enum FileType {
  LOGO(2000000L);

  public final Long size;

  FileType(final Long size) {
    this.size = size;
  }
}
