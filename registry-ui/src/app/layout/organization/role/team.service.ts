import {Injectable, Injector} from '@angular/core';
import {AbstractService} from "../../../common/service/abstract.service";
import {Team} from "../../../common/value/team.value";
import {environment} from "../../../../environments/environment";
import {Organization} from "../organization.value";
import {Member} from "./member.value";

/**
 * Team 서비스
 */
@Injectable()
export class TeamService extends AbstractService {

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Protected Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Variables
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Constructor
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	constructor(protected injector: Injector) {
		super(injector);
	}

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Override Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Public Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/**
	 * Team role 등록/수정
	 * @returns {Promise<any>}
	 */
	public updateTeam(org: string, team: Team.Entity): Promise<Team.Entity> {
		return this.put(`${environment.apiUrl}/organization/${org}/team/${team.name}`, team);
	}

  /**
   * Team role 삭제
   * @returns {Promise<any>}
   */
  public deleteTeamRole(org: string, team: string): Promise<Team.Entity> {
    return this.delete(`${environment.apiUrl}/organization/${org}/team/${team}`);
  }

  /**
   * Team member 조회
   * @returns {Promise<any>}
   */
  public getTeamMemberList(org: string, team: string): Promise<Member.MemberList> {
    return this.get(`${environment.apiUrl}/organization/${org}/members?includePending=true`);
  }

  /**
   * get entities
   * @returns {Promise<any>}
   */
  public searchMember(org: string, username: string): Promise<Member.Results> {
    return this.get(`${environment.apiUrl}/entities/${username}?namespace=${org}`);
  }

  /**
   * create team member
   * @returns {Promise<any>}
   */
  public createTeamMember(org: string, team: string, username: string): Promise<Member.Results> {
    return this.put(`${environment.apiUrl}/organization/${org}/members/${username}`, null);
  }

  /**
   * delete team member
   * @returns {Promise<any>}
   */
  public deleteTeamMember(org: string, team: string, username: string): Promise<Member.Results> {
    return this.delete(`${environment.apiUrl}/organization/${org}/members/${username}`);
  }

  /**
   * 팀 상세 가져오기
   * @param org
   * @returns {Team.Entity[]}
   */
  public getTeamDetail(org: Organization.Entity, teamname: string) {
    let team: Team.Entity;
    this.getTeamList(org).forEach(value => {
      if (value.name == teamname) {
        team = value;
      }
    });

    return team;
  }

  /**
   * 팀 목록 가져오기
   * @param org
   * @returns {Team.Entity[]}
   */
	public getTeamList(org: Organization.Entity) {
	  let teamList: Team.Entity[] = [];

	  org.orderedTeams.forEach(value => {
	    teamList.push(org.teams[value]);
    });

	  return teamList;
  }

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Protected Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    | Private Method
    |-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

}
