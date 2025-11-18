/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.util.Set;

/**
 *
 * @author mohammed
 * @param <T>
 */
public abstract class Pageable<T> {
        public static final int MAX_PAGE_SIZE = 10;
        private final Set<T> t;
        private final int maxPages;
        private final int currentPage;

        public Pageable(Set<T> t, int maxPages, int currentPage) {
            this.t = t;
            this.maxPages = maxPages;
            this.currentPage = currentPage;
        }

        public Set<T> getT() {
            return t;
        }

        public int getMaxPages() {
            return maxPages;
        }

        public int getCurrentPage() {
            return currentPage;
        }
    
}