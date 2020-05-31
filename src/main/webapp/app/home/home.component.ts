import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';

import { LoginModalService } from 'app/core/login/login-modal.service';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/user/account.model';

import dayGridPlugin from '@fullcalendar/daygrid';
import timeGrigPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction'; // for dateClick
import { EventInput } from '@fullcalendar/core';
import { AudiovisualService } from 'app/entities/audiovisual/audiovisual.service';
import { IAudiovisual } from 'app/shared/model/audiovisual.model';
import { HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from 'app/app.constants';
import { JhiEventManager } from 'ng-jhipster';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
  resourceUrl = SERVER_API_URL + '/audiovisual/';
  audiovisuals?: IAudiovisual[];
  calendarPlugins = [dayGridPlugin, timeGrigPlugin, interactionPlugin];
  calendarWeekends = true;
  calendarEvents: EventInput[] = [];

  account: Account | null = null;
  authSubscription?: Subscription;

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private audiovisualService: AudiovisualService,
    private eventManager: JhiEventManager
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => (this.account = account));
    if (this.isAuthenticated()) {
      this.getCalendarData();
    }
    this.registerAuthenticationSuccess();
  }

  isAuthenticated(): boolean {
    return this.accountService.isAuthenticated();
  }

  login(): void {
    this.loginModalService.open();
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  registerAuthenticationSuccess(): void {
    this.eventManager.subscribe('authenticationSuccess', () => {
      this.authSubscription = this.accountService.getAuthenticationState().subscribe(account => {
        this.account = account;
        if (this.isAuthenticated()) {
          this.getCalendarData();
        }
      });
    });
  }

  getCalendarData(): void {
    this.audiovisualService.query().subscribe((res: HttpResponse<IAudiovisual[]>) => this.onSuccess(res.body));
  }

  protected onSuccess(data: IAudiovisual[] | null): void {
    const noDone = 0;
    this.audiovisuals = data || [];
    this.calendarEvents = this.audiovisuals
      .filter(obj => {
        return obj.check === noDone;
      })
      .map(item => {
        return {
          title: item.title,
          start: item.startDate?.toDate(),
          end: item.deadline?.toDate(),
          url: this.resourceUrl + item.id + '/' + 'edit',
          allDay: true
        };
      });
  }
}
