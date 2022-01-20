import { SelectionModel } from '@angular/cdk/collections';
import { Identifiers } from '@angular/compiler';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { DefaultService } from 'src/service';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss']
})
export class NotificationComponent implements OnInit {

  constructor(private service: DefaultService, private router: ActivatedRoute) { }

  notificationId?: string;


  displayedColumns = ["identifier", "delivered"]

  dataSource: any = []


  ngOnInit(): void {
    this.router.paramMap.subscribe(({ params }: any) => {
      this.notificationId = params.id

      this.loadNotification();
    });
  }

  async loadNotification() {
    if (!this.notificationId) return;

    let response = await firstValueFrom(this.service.statsIdGet(this.notificationId))
    this.dataSource = response.stats
  }


}
