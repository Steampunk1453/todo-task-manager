import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'audiovisual',
        loadChildren: () => import('./audiovisual/audiovisual.module').then(m => m.ToDoTaskManagerAudiovisualModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class ToDoTaskManagerEntityModule {}
