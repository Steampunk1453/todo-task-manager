import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ToDoTaskManagerSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';

import { FullCalendarModule } from '@fullcalendar/angular'; // for FullCalendar!

@NgModule({
  imports: [ToDoTaskManagerSharedModule, RouterModule.forChild([HOME_ROUTE]), FullCalendarModule],
  declarations: [HomeComponent]
})
export class ToDoTaskManagerHomeModule {}
