import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';

export interface FilterOptions {
  boards: string[];
  mediums: string[];
  grades: string[];
  subjects: string[];
}

export interface FilterRequest {
  board?: string;
  medium?: string[];
  grade?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class FilterOptionsService {
  private baseUrl = environment.baseUrl;

  constructor(private http: HttpClient) {}

  // ✅ Get all filter options
  getFilterOptions(): Observable<FilterOptions> {
    return this.http
      .get<FilterOptions>(`${this.baseUrl}/filters/options`)
      .pipe(
        map(options => ({
          boards: this.makeDistinct(this.flattenResponse(options.boards)),
          mediums: this.makeDistinct(this.flattenResponse(options.mediums)),
          grades: this.makeDistinct(this.flattenResponse(options.grades)),
          subjects: this.makeDistinct(this.flattenResponse(options.subjects))
        }))
      );
  }

  // ✅ Boards
  getBoards(): Observable<string[]> {
    return this.http
      .get<string[]>(`${this.baseUrl}/filters/boards`)
      .pipe(map(data => this.makeDistinct(this.flattenResponse(data))));
  }

  // ✅ Mediums based on board
  getMediums(board: string): Observable<string[]> {
    const body: FilterRequest = { board };
    return this.http
      .post<string[]>(`${this.baseUrl}/filters/mediums`, body)
      .pipe(map(data => this.makeDistinct(this.flattenResponse(data))));
  }

  // ✅ Grades based on board + medium
  getGrades(board: string, mediums: string[]): Observable<string[]> {
    const body: FilterRequest = { board, medium: mediums };
    return this.http
      .post<string[]>(`${this.baseUrl}/filters/grades`, body)
      .pipe(map(data => this.makeDistinct(this.flattenResponse(data))));
  }

  // ✅ Subjects based on board + medium + grade
  getSubjects(board: string, mediums: string[], grades: string[]): Observable<string[]> {
    const body: FilterRequest = { board, medium: mediums, grade: grades };
    return this.http
      .post<string[]>(`${this.baseUrl}/filters/subjects`, body)
      .pipe(map(data => this.makeDistinct(this.flattenResponse(data))));
  }

  // ✅ Aliases (optional but safe to keep)
  getMediumsByBoard(request: any): Observable<string[]> {
    return this.http
      .post<string[]>(`${this.baseUrl}/filters/mediums`, request)
      .pipe(map(data => this.makeDistinct(this.flattenResponse(data))));
  }

  getGradesByBoardAndMedium(request: any): Observable<string[]> {
    return this.http
      .post<string[]>(`${this.baseUrl}/filters/grades`, request)
      .pipe(map(data => this.makeDistinct(this.flattenResponse(data))));
  }

  getSubjectsByBoardMediumAndGrade(request: any): Observable<string[]> {
    return this.http
      .post<string[]>(`${this.baseUrl}/filters/subjects`, request)
      .pipe(map(data => this.makeDistinct(this.flattenResponse(data))));
  }

  /**
   * ✅ Flatten nested/encoded responses.
   * Handles cases like ["[\"9\",\"10\"]"] → ["9","10"]
   */
  private flattenResponse = (data: any): string[] => {
    if (!data) return [];
    if (!Array.isArray(data)) return [String(data)];

    return data.flatMap((item: any) => {
      if (typeof item === 'string') {
        try {
          const parsed = JSON.parse(item);
          return Array.isArray(parsed) ? parsed : [parsed];
        } catch {
          if (item.includes(',')) {
            return item.split(',').map(v => v.trim());
          }
          return [item.trim()];
        }
      } else if (Array.isArray(item)) {
        return item.map(v => String(v).trim());
      } else {
        return [String(item).trim()];
      }
    });
  };

  /**
   * ✅ Remove duplicates safely (forces to string before trimming)
   */
  private makeDistinct = (arr: any[]): string[] => {
    return [...new Set(arr.map(a => String(a).trim()))];
  };
}
