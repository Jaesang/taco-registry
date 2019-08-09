import {Image} from "./image.value";
import {Page} from "../../../common/value/result-value";
export namespace Tag {

	export class Entity {
    public dockerImageId: string;
    public lastModified: string;
    public manifestDigest: string;
    public name: string;
    public size: number;
    public expiration: any;

    public formattedLastModified: string;
    public formattedExpiration: string;
    public checked: boolean = false;
	}

	export class History {
	  public dockerImageId: string;
    public endTs: number;
    public manifestDigest: string;
    public name: string;
    public reversion: boolean;
    public startTs: number;

    public date: number;
    public formattedDate: string;
    public isMoved: boolean;
    public isDeleted: boolean;
    public beforeHistory: History;
  }

	export class Security {
	  public data: Data;
	  public status: string;
    public vulnerabilityHighCount: number;
    public fixableCount: number;
    public fixablePackageCount: number;
  }

  export class Data {
	  public Layer: Tag.Layer;
  }

  export class Layer {
    public Features: Feature[] = [];
    public IndexedByVersion: number;
    public Name: string;
    public NamespaceName: string;
    public ParentName: string;
  }

  export class Feature {
    public AddedBy: string;
    public Name: string;
    public NamespaceName: string;
    public Version: string;
    public VersionFormat: string;
    public Vulnerabilities: Vulnerability[] = [];

    public vScore: number;
    public vCount: number;
    public vHighCount: number;
    public vMediumCount: number;
    public vLowCount: number;
    public vNegiCount: number;
    public vUnknownCount: number;

    public remainScore: number;
    public remainCount: number;
    public remainHighCount: number;
    public remainMediumCount: number;
    public remainLowCount: number;
    public remainNegiCount: number;
    public remainUnknownCount: number;

    public upgradeScore: number;

    public expand: boolean;
    public layer: Image.Layer;
  }

  export class Vulnerability {
    public Description: string;
    public FixedBy: string;
    public Link: string;
    public Name: string;
    public NamespaceName: string;
    public Severity: VulnerabilityType;

    public packageName;
    public currentVersion;
    public score: number;
    public expand: boolean;
    public layer: Image.Layer;
    public isFixed: boolean;
  }

  export enum VulnerabilityType {
    High = <any>'High',
    Medium = <any>'Medium',
    Low = <any>'Low',
    Negligible = <any>'Negligible',
    Unknown = <any>'Unknown'
  }

  export enum VulnerabilityScore {
    High = <any>9,
    Medium = <any>6,
    Low = <any>3,
    Negligible = <any>1,
    Unknown = <any>0
  }

  export class HistoryResult extends Page {
    public content: History[];
  }

}
