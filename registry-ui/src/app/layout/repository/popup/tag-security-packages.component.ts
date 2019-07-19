import {Component, OnInit, ElementRef, Injector, Input, ViewChild} from "@angular/core";
import {AbstractComponent} from "../../../common/component/abstract.component";
import {BuildHistoryService} from "../build-history/build-history.service";
import {RepositoryService} from "../repository.service";
import {Tag} from "../tag-info/tag.value";
import {Image} from "../tag-info/image.value";
import Vulnerability = Tag.Vulnerability;

declare var echarts: any;

@Component({
  selector: '[tag-security-packages]',
  templateUrl: 'tag-security-packages.component.html'
})
export class TagSecurityPackagesComponent extends AbstractComponent implements OnInit {

  @ViewChild('searchInput')
  public searchInput: ElementRef;

  @Input()
  public security: Tag.Security;

  @Input()
  public layerList: Image.Layer[] = [];

  public sortProperty: string = 'upgradeScore';
  public sortDirection: string = 'desc';

  public searchKey: string;

  public onlyFixed: boolean = false;

  public packageList: Tag.Feature[] = [];

  public colorCodes = ['#b7b9c2', '#b7b9c2', '#fcc419', '#fd7e14', '#f8523f'];

  public packageCount: number = 0;
  public highCount: number = 0;
  public mediumCount: number = 0;
  public lowCount: number = 0;
  public negiCount: number = 0;
  public unknownCount: number = 0;
  public noCount: number = 0;

  private chart: any;

  constructor(protected elementRef: ElementRef,
              protected injector: Injector,
              private repositoryService: RepositoryService,
              private buildHistoryService: BuildHistoryService) {

    super(elementRef, injector);
  }

  ngOnInit() {
  }

  public init(security: Tag.Security, layerList: Image.Layer[]) {
    this.security = security;
    this.layerList = layerList;
    this.searchKey = '';
    this.searchInput.nativeElement.value = '';

    this.getPackageList();
  }

  /**
   * 정렬
   * @param colName
   * @param sort
   */
  public sortClick(property: string) {
    if (this.sortProperty == property) {
      this.sortDirection = this.sortDirection == 'desc' ? 'asc' : 'desc';
    } else {
      this.sortDirection = 'desc';
    }
    this.sortProperty = property;
  }

  public clearSearchClick() {
    this.searchKey = '';
  }

  /**
   * 취약점 목록
   */
  public getPackageList() {
    this.packageCount = 0;
    this.highCount = 0;
    this.mediumCount = 0;
    this.lowCount = 0;
    this.negiCount = 0;
    this.unknownCount = 0;
    this.noCount = 0;

    let highestUpgradeScore = 0;

    if (this.chart) {
      this.chart.dispose();
      this.chart = null;
    }

    let list = [];
    if (this.security.status != 'queued') {
      this.security.data.Layer.Features.forEach(value => {
        list.push(value);
        this.packageCount++;

        value.vCount = 0;
        value.vHighCount = 0;
        value.vMediumCount = 0;
        value.vLowCount = 0;
        value.vNegiCount = 0;
        value.vUnknownCount = 0;
        value.vScore = 0;

        value.remainCount = 0;
        value.remainHighCount = 0;
        value.remainMediumCount = 0;
        value.remainLowCount = 0;
        value.remainNegiCount = 0;
        value.remainUnknownCount = 0;
        value.remainScore = 0;

        value.upgradeScore = 0;

        value.expand = false;
        value.layer = this.getLayerByImageId(value.AddedBy);

        if (value.Vulnerabilities && value.Vulnerabilities.length) {
          value.Vulnerabilities.forEach(v => {
            value.vCount++;

            if (!v.FixedBy) {
              value.remainCount++;
            }

            if (v.Severity == Tag.VulnerabilityType.High) {
              value.vHighCount++;
              value.vScore += Tag.VulnerabilityScore.High;
              if (!v.FixedBy) {
                value.remainHighCount++;
                value.remainScore += Tag.VulnerabilityScore.High;
              } else {
                value.upgradeScore += Tag.VulnerabilityScore.High;
              }
            } else if (v.Severity == Tag.VulnerabilityType.Medium) {
              value.vMediumCount++;
              value.vScore += Tag.VulnerabilityScore.Medium;
              if (!v.FixedBy) {
                value.remainMediumCount++;
                value.remainScore += Tag.VulnerabilityScore.Medium;
              } else {
                value.upgradeScore += Tag.VulnerabilityScore.Medium;
              }
            } else if (v.Severity == Tag.VulnerabilityType.Low) {
              value.vLowCount++;
              value.vScore += Tag.VulnerabilityScore.Low;
              if (!v.FixedBy) {
                value.remainLowCount++;
                value.remainScore += Tag.VulnerabilityScore.Low;
              } else {
                value.upgradeScore += Tag.VulnerabilityScore.Low;
              }
            } else if (v.Severity == Tag.VulnerabilityType.Negligible) {
              value.vNegiCount++;
              value.vScore += Tag.VulnerabilityScore.Negligible;
              if (!v.FixedBy) {
                value.remainNegiCount++;
                value.remainScore += Tag.VulnerabilityScore.Negligible;
              } else {
                value.upgradeScore += Tag.VulnerabilityScore.Negligible;
              }
            } else if (v.Severity == Tag.VulnerabilityType.Unknown) {
              value.vUnknownCount++;
              value.vScore += Tag.VulnerabilityScore.Unknown;
              if (!v.FixedBy) {
                value.remainUnknownCount++;
                value.remainScore += Tag.VulnerabilityScore.Unknown;
              } else {
                value.upgradeScore += Tag.VulnerabilityScore.Unknown;
              }
            }
          });
        } else {
          this.noCount++;
        }

        if (value.vHighCount) {
          this.highCount++;
          value.vScore = value.vScore + 10000;
        } else if (value.vMediumCount) {
          this.mediumCount++;
          value.vScore = value.vScore + 1000;
        } else if (value.vLowCount) {
          this.lowCount++;
          value.vScore = value.vScore + 100;
        } else if (value.vNegiCount) {
          this.negiCount++;
          value.vScore = value.vScore + 10;
        } else if (value.vUnknownCount) {
          this.unknownCount++;
        }

        if (value.remainHighCount) {
          value.remainScore = value.remainScore + 10000;
        } else if (value.remainMediumCount) {
          value.remainScore = value.remainScore + 1000;
        } else if (value.remainLowCount) {
          value.remainScore = value.remainScore + 100;
        } else if (value.remainNegiCount) {
          value.remainScore = value.remainScore + 10;
        } else if (value.remainUnknownCount) {
        }

        if (highestUpgradeScore < value.upgradeScore) {
          highestUpgradeScore = value.upgradeScore;
        }
      });

      this.security.data.Layer.Features.forEach(value => {
        if (value.upgradeScore) {
          value.upgradeScore = value.upgradeScore / highestUpgradeScore * 100;
        }
      });

      this.createChart();
    }

    this.packageList = list;
  }

  /**
   * layer 정보 조회
   * @param imageId
   * @returns {Image.Layer}
   */
  private getLayerByImageId(imageId: string) {
    let layer = new Image.Layer();
    imageId = imageId.split('.')[0];

    this.layerList.forEach(value => {
      if (value.id == imageId) {
        layer = value;
      }
    });

    return layer;
  }

  /**
   * chart 생성
   */
  private createChart() {

    if (!this.chart) {
      var dom = document.getElementById('chartPackages');
      this.chart = echarts.init(dom);
    }

    let data = [];
    if (this.highCount) {
      data.push({name: Tag.VulnerabilityType.High, value: this.highCount, itemStyle: {
        color: this.colorCodes[4]
      }});
    }

    if (this.mediumCount) {
      data.push({name: Tag.VulnerabilityType.Medium, value: this.mediumCount, itemStyle: {
        color: this.colorCodes[3]
      }});
    }

    if (this.lowCount) {
      data.push({name: Tag.VulnerabilityType.Low, value: this.lowCount, itemStyle: {
        color: this.colorCodes[2]
      }});
    }

    if (this.negiCount) {
      data.push({name: Tag.VulnerabilityType.Negligible, value: this.negiCount, itemStyle: {
        color: this.colorCodes[1]
      }});
    }

    if (this.noCount) {
      data.push({name: 'None', value: this.noCount, itemStyle: {
        color: '#40c057'
      }});
    }

    // if (this.unknownCount) {
    //   data.push({name: Tag.VulnerabilityType.Unknown, value: this.unknownCount, itemStyle: {
    //     color: this.colorCodes[0]
    //   }});
    // }

    let series = [
      {
        type:'pie',
        radius: ['60%', '100%'],
        avoidLabelOverlap: false,
        hoverAnimation: false,
        label: {
          normal: {
            show: true,
            formatter: (params) => {
              return Math.round(params.percent) + '%';
            },
            position: 'inside'
          }
        },
        labelLine: {
          normal: {
            show: false
          }
        },
        data: data
      }
    ];

    let option = {
      tooltip: {
        trigger: 'item',
        formatter: (params) => {
          return `${params.name}<br/>${params.value} (${Math.round(params.percent)}%)`;
        },
      },
      series: series
    };

    this.chart.setOption(option, true);
  }
}
