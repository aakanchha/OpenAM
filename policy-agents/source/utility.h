/**
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2014 - 2015 ForgeRock AS.
 */

#ifndef UTILITY_H
#define UTILITY_H

#include "pcre.h"

#define AM_CACHE_TIMEFORMAT "%Y-%m-%d %H:%M:%S"
#define ARRAY_SIZE(array) sizeof(array) / sizeof(array[0])

enum {
    AM_EXACT_MATCH = 0,
    AM_EXACT_PATTERN_MATCH,
    AM_NO_MATCH
};

enum {
    SCOPE_SELF = 0,
    SCOPE_SUBTREE,
    SCOPE_RESPONSE_ATTRIBUTE_ONLY
};

enum {
    SET_ATTRS_NONE = 0,
    SET_ATTRS_AS_HEADER,
    SET_ATTRS_AS_COOKIE
};

/* shared memory area handle */

typedef struct {
#ifdef _WIN32
    HANDLE h[3]; /* 0: mutex, 1: file, 2: file mapping */
    DWORD error;
#else
    int fd;
    void *lock;
    int error;
#endif
    void *pool;
    void *user;
    char init;
    char name[3][AM_PATH_SIZE];
} am_shm_t;

void am_shm_set_user_offset(am_shm_t *r, size_t s);
void display_info(am_shm_t *am);

struct am_cookie {
    char *name;
    char *value;
    char *domain;
    char *max_age;
    char *path;
    char is_secure;
    char is_http_only;
    struct am_cookie *next;
};

void delete_am_cookie_list(struct am_cookie **list);

struct am_action_decision {
    char method;
    char action;
    unsigned long long ttl;
    struct am_namevalue *advices;
    struct am_action_decision *next;
};

struct am_policy_result {
    int index;
    int scope;
    char *resource;
    struct am_namevalue *response_attributes;
    struct am_namevalue *response_decisions; /*profile attributes*/
    struct am_action_decision *action_decisions;
    struct am_policy_result *next;
};

void delete_am_policy_result_list(struct am_policy_result **list);

struct notification_worker_data {
    unsigned long instance_id;
    char *post_data;
    size_t post_data_sz;
};

struct logout_worker_data {
    unsigned long instance_id;
    char *token;
    char *openam;
    struct am_ssl_options info;
};

typedef struct {
    uint64_t start;
    uint64_t stop;
    uint64_t freq;
    int state;
} am_timer_t;

void am_timer_start(am_timer_t *t);
void am_timer_stop(am_timer_t *t);
void am_timer_pause(am_timer_t *t);
void am_timer_resume(am_timer_t *t);
double am_timer_elapsed(am_timer_t *t);
void am_timer_report(unsigned long instance_id, am_timer_t *t, const char *op);

int ip_address_match(const char *ip, const char **list, unsigned int listsize, unsigned long instance_id);

am_status_t get_token_from_url(am_request_t *rq);
am_status_t get_cookie_value(am_request_t *rq, const char *separator, const char *cookie_name,
        const char *cookie_header_val, char **value);
int parse_url(const char *u, struct url *url);
char *url_encode(const char *str);
char *url_decode(const char *str);

int am_bin_path(char* buffer, size_t len);

int string_replace(char **original, const char *pattern, const char *replace, size_t *sz);

void am_shm_unlock(am_shm_t *);
int am_shm_lock(am_shm_t *);
am_shm_t *am_shm_create(const char *, size_t);
void am_shm_shutdown(am_shm_t *);
void *am_shm_alloc(am_shm_t *am, size_t usize);
void am_shm_free(am_shm_t *am, void *ptr);
void *am_shm_realloc(am_shm_t *am, void *ptr, size_t size);
char *am_shm_strdup(am_shm_t *am, const char *str);

int am_create_agent_dir(const char *sep, const char *path, char **created_name, char **created_name_simple);

int decrypt_password(const char *key, char **password);
int encrypt_password(const char *key, char **password);

char is_big_endian();
size_t page_size(size_t size);
int match(unsigned long instance_id, const char *subject, const char *pattern);
char *match_group(pcre *x, int cg, const char *subject, size_t *len);
int gzip_deflate(const char *uncompressed, size_t *uncompressed_sz, char **compressed);
int gzip_inflate(const char *compressed, size_t *compressed_sz, char **uncompressed);
void trim(char *a, char w);

int am_asprintf(char **buffer, const char *fmt, ...);
int am_vasprintf(char **buffer, const char *fmt, va_list arg);

void read_directory(const char *path, struct am_namevalue **list);

#ifndef _AIX
char *strndup(const char *s, size_t n);
size_t strnlen(const char *string, size_t maxlen);
#endif

char *stristr(char *str1, char *str2);
int concat(char **str, size_t *str_sz, const char *s2, size_t s2sz);

int copy_file(const char *from, const char *to);

void xml_entity_escape(char *temp_str, size_t str_len);

int char_count(const char *string, int c, int *last);
void uuid(char *buf, size_t buflen);

char file_exists(const char *fn);
char *load_file(const char *filepath, size_t *data_sz);
ssize_t write_file(const char *filepath, const void *data, size_t data_sz);

int get_line(char **line, size_t *size, FILE *file);

int am_session_decode(am_request_t *r);

char policy_compare_url(am_request_t *r, const char *pattern, const char *resource);
const char *am_policy_strerror(char status);

char *am_strsep(char **sp, const char *sep);

int compare_property(const char *line, const char *property);

int am_make_path(const char *path);
int am_delete_file(const char *fn);
int am_delete_directory(const char *path);

int get_valid_url_index(unsigned long instance_id);
void set_valid_url_index(unsigned long instance_id, int value);

int create_am_namevalue_node(const char *n, size_t ns, const char *v, size_t vs, struct am_namevalue **node);
int create_am_policy_result_node(const char *va, size_t vs, struct am_policy_result **node);
int create_am_action_decision_node(char a, char m, unsigned long long ttl,
        struct am_action_decision **node);

int am_agent_logout(unsigned long instance_id, const char *openam,
        const char *token, struct am_ssl_options *info, void(*log)(const char *, ...));
int am_agent_naming_request(unsigned long instance_id, const char *openam, const char *token);
int am_agent_session_request(unsigned long instance_id, const char *openam,
        const char *token, const char *user_token, const char *notif_url);
int am_agent_policy_request(unsigned long instance_id, const char *openam,
        const char *token, const char *user_token, const char *req_url,
        const char *notif_url, const char *scope, const char *cip, const char *pattr,
        struct am_ssl_options *info,
        struct am_namevalue **session_list,
        struct am_policy_result **policy_list);

int am_scope_to_num(const char *scope);
const char *am_scope_to_str(int scope);

int remove_cookie(am_request_t *rq, const char *cookie_name, char **cookie_hdr);

int am_get_policy_cache_entry(am_request_t *r, const char *key);
int am_add_policy_cache_entry(am_request_t *r, const char *key, int valid);

int am_get_agent_config(unsigned long instance_id, const char *config_file, am_config_t **cnf);

void remove_agent_instance_byname(const char *name);

void am_agent_init_set_value(unsigned long instance_id, char lock, int val);
int am_agent_init_get_value(unsigned long instance_id, char lock);
int am_agent_instance_init_init();
void am_agent_instance_init_lock();
void am_agent_instance_init_unlock();
void am_agent_instance_init_release(char unlink);

#endif
