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
    public display_name: string;
    public repository: Repository.Entity;
    public subdirectory: string;
    public started: string;
    public tags: Array<string>;
    public archive_url: string;
    public pull_robot: Object;
    public trigger: string;
    public trigger_metadata: string;
    public context: string;
    public is_writer: boolean;
    public phase: Phase;
    public resource_key: string;
    public manual_user: string;
    public id: string;
    public dockerfile_path: string;

    public formatted_started: string;
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
    public logs_url: string;
  }
}
