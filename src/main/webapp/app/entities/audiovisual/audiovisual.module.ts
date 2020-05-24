import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ToDoTaskManagerSharedModule } from 'app/shared/shared.module';
import { AudiovisualComponent } from './audiovisual.component';
import { AudiovisualDetailComponent } from './audiovisual-detail.component';
import { AudiovisualUpdateComponent } from './audiovisual-update.component';
import { AudiovisualDeleteDialogComponent } from './audiovisual-delete-dialog.component';
import { audiovisualRoute } from './audiovisual.route';

@NgModule({
  imports: [ToDoTaskManagerSharedModule, RouterModule.forChild(audiovisualRoute)],
  declarations: [AudiovisualComponent, AudiovisualDetailComponent, AudiovisualUpdateComponent, AudiovisualDeleteDialogComponent],
  entryComponents: [AudiovisualDeleteDialogComponent]
})
export class ToDoTaskManagerAudiovisualModule {}
