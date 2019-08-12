import {Repository} from "../repository.value";

export namespace Build {

  /**
   * phase
   */
  export enum Phase {
    complete = <any>'complete',
    error = <any>'error',
    cancelled = <any>'cancelled',
    waiting = <any>'waiting',
    pulling = <any>'pulling',
    pushing = <any>'pushing',
    building = <any>'building'
  }

  /**
   * log type
   */
  export enum LogType {
    phase = <any>'phase',
    command = <any>'command',
    error = <any>'error'
  }

  /**
   * log phase
   */
  export enum LogPhase {
    build_scheduled = <any>'build-scheduled',
    unpacking = <any>'unpacking',
    pulling = <any>'pulling',
    checking_cache = <any>'checking-cache',
    priming_cache = <any>'priming-cache',
    building = <any>'building',
    pushing = <any>'pushing',
    complete = <any>'complete',
    error = <any>'error'
  }

	export class Entity {
    public status: Object;
    public error: string;
    public displayName: string;
    public repository: Repository.Entity;
    public subdirectory: string;
    public started: string;
    public tags: Array<string>;
    public archiveUrl: string;
    public trigger: string;
    public triggerMetadata: string;
    public context: string;
    public isWriter: boolean;
    public phase: Phase;
    public resourceKey: string;
    public manualUser: string;
    public id: string;
    public dockerfilePath: string;

    public dockerfile: string;
    public gitPath: string;
    public gitUsername: string;
    public gitPassword: string;

    public formattedStarted: string;
	}

	export class Log {
    public data: any;
    public message: string;
    public type: LogType;

    public children: Log[];
    public command: string;
    public commandName: string;
    public expand: boolean;
  }

	export class Result {
   public builds: Entity[];
  }

  export class LogResult {
    public logs: Log[];
    public start: number;
    public total: number;
    public logsUrl: string;
  }
}
