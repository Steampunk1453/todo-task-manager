import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { AudiovisualService } from 'app/entities/audiovisual/audiovisual.service';
import { IAudiovisual, Audiovisual } from 'app/shared/model/audiovisual.model';

describe('Service Tests', () => {
  describe('Audiovisual Service', () => {
    let injector: TestBed;
    let service: AudiovisualService;
    let httpMock: HttpTestingController;
    let elemDefault: IAudiovisual;
    let expectedResult: IAudiovisual | IAudiovisual[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(AudiovisualService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Audiovisual(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', currentDate, currentDate, 0);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            startDate: currentDate.format(DATE_TIME_FORMAT),
            deadline: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Audiovisual', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            startDate: currentDate.format(DATE_TIME_FORMAT),
            deadline: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            startDate: currentDate,
            deadline: currentDate
          },
          returnedFromService
        );

        service.create(new Audiovisual()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Audiovisual', () => {
        const returnedFromService = Object.assign(
          {
            title: 'BBBBBB',
            genre: 'BBBBBB',
            platform: 'BBBBBB',
            platformUrl: 'BBBBBB',
            startDate: currentDate.format(DATE_TIME_FORMAT),
            deadline: currentDate.format(DATE_TIME_FORMAT),
            check: 1
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            startDate: currentDate,
            deadline: currentDate
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Audiovisual', () => {
        const returnedFromService = Object.assign(
          {
            title: 'BBBBBB',
            genre: 'BBBBBB',
            platform: 'BBBBBB',
            platformUrl: 'BBBBBB',
            startDate: currentDate.format(DATE_TIME_FORMAT),
            deadline: currentDate.format(DATE_TIME_FORMAT),
            check: 1
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            startDate: currentDate,
            deadline: currentDate
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Audiovisual', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
