import {Injectable, Injector} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {Lnb} from '../../layout/lnb/lnb.value';
import {AbstractService} from "./abstract.service";
import {environment} from "../../../environments/environment";
import {RequestOptions, Headers} from "@angular/http";

@Injectable()
export class DockerService extends AbstractService {

  constructor(protected injector: Injector) {
    super(injector);
  }

  public validDockerFile(contents: string): boolean {
    return contents.indexOf('FROM ') == -1 ? false : true;
  }

  public getRegistryBaseImage(contents: string): string | null {
    var baseImage = this.getBaseImage(contents);
    if (!baseImage) {
      return null;
    }

    if (baseImage.indexOf(`${environment.host}/`) != 0) {
      return null;
    }

    return baseImage.substring(<number>environment.host.length + 1);
  }

  public getBaseImage(contents: string): string | null {
    const imageAndTag = this.getBaseImageAndTag(contents);
    if (!imageAndTag) {
      return null;
    }

    // Note, we have to handle a few different cases here:
    // 1) someimage
    // 2) someimage:tag
    // 3) host:port/someimage
    // 4) host:port/someimage:tag
    const lastIndex: number = imageAndTag.lastIndexOf(':');
    if (lastIndex == -1) {
      return imageAndTag;
    }

    // Otherwise, check if there is a / in the portion after the split point. If so,
    // then the latter is part of the path (and not a tag).
    const afterColon: string = imageAndTag.substring(lastIndex + 1);
    if (afterColon.indexOf('/') != -1) {
      return imageAndTag;
    }

    return imageAndTag.substring(0, lastIndex);
  }

  public getBaseImageAndTag(contents: string): string | null {
    var baseImageAndTag: string = null;

    const fromIndex: number = contents.indexOf('FROM ');
    if (fromIndex != -1) {
      var newlineIndex: number = contents.indexOf('\n', fromIndex);
      if (newlineIndex == -1) {
        newlineIndex = contents.length;
      }

      baseImageAndTag = contents.substring(fromIndex + 'FROM '.length, newlineIndex).trim();
    }

    return baseImageAndTag;
  }
}
