
local BASE_DIR = "/home/data"

local lfs = require("lfs")
local cjson = require("cjson")
local upload = require "resty.upload"

local file_list_data = {}

local function lua_string_split(str, split_char)
	local sub_str_tab = {}
	local i = 0
	local j = 0
	while true do
		j = string.find(str, split_char,i+1)
		if j == nil then
			table.insert(sub_str_tab,string.sub(str,i+1,#str))
			break
		end
		if i+1 <= j-1 then
			table.insert(sub_str_tab,string.sub(str,i+1,j-1))
		end
		i = j
		if i == #str then
			break
		end
	end
	return sub_str_tab
end

function os.capture(cmd, raw)
	local f = assert(io.popen(cmd, 'r'))
	local s = assert(f:read('*a'))
	f:close()
	if raw then return s end
	s = string.gsub(s, '^%s+', '')
	s = string.gsub(s, '%s+$', '')
	s = string.gsub(s, '[\n\r]+', ' ')
	return s
end

local function directory_exists(sPath)
	if type(sPath) ~= "string" then return false end

	local response = os.execute( "cd " .. sPath )
	if response == 0 then
		return true
	end
	return false
end

function os.rmdir(path)
	ngx.log(ngx.ERR, "os.rmdir: " .. path)
	if directory_exists(path) then
		local function _rmdir(path)
			local iter, dir_obj = lfs.dir(path)
			while true do
				local dir = iter(dir_obj)
				if dir == nil then break end
				if dir ~= "." and dir ~= ".." then
					local curDir = path..dir
					local mode = lfs.attributes(curDir, "mode") 
					if mode == "directory" then
						_rmdir(curDir.."/")
					elseif mode == "file" then
						os.remove(curDir)
					end
				end
			end
			local succ, des = os.remove(path)
			if des then 
				print(des) 
			end
			return succ
		end
		_rmdir(path)
	end
	return true
end

local function directory_exists(sPath)
	if type(sPath) ~= "string" then return false end

	local response = os.execute( "cd " .. sPath )
	if response == 0 then
		return true
	end
	return false
end


local function get_modification(path)
	return lfs.attributes(path)
end

local function get_type(path)
	return lfs.attributes(path).mode
end

local function get_size(path)
	return lfs.attributes(path)
end

local function is_dir(path)
	return get_type(path) == "directory"
end

local function findx(str,x)
	for i = 1,#str do
		if string.sub(str,-i,-i) == x then
			return -i
		end
	end
end

local function get_name(str)
	return string.sub(str, findx(str,"/") + 1,-1)
end

local function scan_dir(relative_path)
	local path = BASE_DIR .. relative_path
	
	for f in lfs.dir(path) do
		p = path .. "/" .. f
		t = {}
		if f ~= "." and f ~= '..' then
			if is_dir(p) then
				t["name"] = f
				t["type"] = "dir"
				t["size"] = "0kb"
				local modify_val = get_modification(p)
				if modify_val == nil then
					t["status"] = 1
					t["modify"] = "null"
				else
					t["status"] = 0
					t["modify"] = modify_val.modification
				end
				
				t["path"] = relative_path  .. f .. "/"
				table.insert(file_list_data, t)
				--scan_dir(p, t[f]) -- loop scan
			else
				t["name"] = f
				t["type"] = "file"
				
				local size_val = get_size(p)
				if size_val == nil then
					t["status"] = 1
					t["size"] = "null"
				else
					t["status"] = 0
					t["size"] = size_val.size .. "b"
				end

				local file_modify_val = get_modification(p)
				
				if file_modify_val == nil then
					t["status"] = 1
					t["modify"] = "null"
				else
					t["status"] = 0
					t["modify"] = file_modify_val.modification
				end
				
				t["path"] = relative_path .. f
				table.insert(file_list_data, t)
			end  
			
		end
	end
end


local function get_server_file_list(relative_path)
	scan_dir(ngx.unescape_uri(relative_path))
	ngx.log(ngx.ERR, cjson.encode(file_list_data))
	ngx.print(cjson.encode(file_list_data))
end


local function mkdir(dir)
	local res_data = {}
	local err, des = lfs.mkdir(BASE_DIR .. ngx.unescape_uri(dir))
	if err == true then
		res_data["status"] = 0
	else
		res_data["status"] = 1
		ngx.log(ngx.ERR, "failed to mkdir: ", des)
	end
end

local function rm_file(path)
	local res_data = {}
	ngx.log(ngx.ERR, BASE_DIR .. ngx.unescape_uri(path))
	if is_dir(BASE_DIR .. ngx.unescape_uri(path)) then
		-- local err, des = os.rmdir(BASE_DIR .. path)
		local rm_cmd = "rm -f -R " .. BASE_DIR .. ngx.unescape_uri(path)
		err = os.capture(rm_cmd, false)
		-- ngx.log(ngx.ERR, err)
		if err == "" then
			res_data["status"] = 0
		else
			res_data["status"] = 1
			ngx.log(ngx.ERR, "failed to rm")
		end
		ngx.print(cjson.encode(res_data))
	else
		local err, des = os.remove(BASE_DIR .. ngx.unescape_uri(path))
		if err == true then
			res_data["status"] = 0
		else
			res_data["status"] = 1
			ngx.log(ngx.ERR, "failed to rm: ", des)
		end
		ngx.print(cjson.encode(res_data))
	end
	
end

local function rename(old_path, new_path)
	local res_data = {}
	local err, des = os.rename(BASE_DIR .. ngx.unescape_uri(old_path), BASE_DIR .. "/" .. ngx.unescape_uri(new_path))
	if err == true then
		res_data["status"] = 0
	else
		res_data["status"] = 1
		ngx.log(ngx.ERR, "failed to rename: ", des)
	end
	ngx.print(cjson.encode(res_data))
end


local function upload_file(relative_path)
	local res_data = {}
	local chunk_size = 4096 -- should be set to 4096 or 8192
	local file = nil
	res_data["status"] = 1

	local form, err = upload:new(chunk_size)
	if not form then
		ngx.log(ngx.ERR, "failed to new upload: ", err)
		--res_data["status"] = 1
		ngx.exit(500)
	end

	form:set_timeout(1000) -- 1 sec
	
	--local tmp_t = lua_string_split(relative_path, "/")
	
	--local file_name = tmp_str[table.getn(tmp_t)]

	local osfilepath = BASE_DIR .. "/" .. ngx.unescape_uri(relative_path)

	while true do
		local type, res, err = form:read()
		if not type then
			ngx.log(ngx.ERR, "failed to read: " .. err)
			--res_data["status"] = 1
			ngx.exit(500)
		end

		-- ngx.say("read: ", cjson.encode({type, res}))

		if type == "header" then
			local filepath = osfilepath
			file = io.open(filepath, "w+")
			if not file then
				ngx.log(ngx.ERR, "failed to open file: " .. filepath)
				--res_data["status"] = 1
				ngx.exit(500)
			end
		elseif type == "body" then
			-- ngx.say("body begin")
			if file then
				file:write(res)
				-- ngx.say("write ok: ", res)
			end
		elseif type == "part_end" then
			-- ngx.say("part_end")
			if file then
				file:close()
				file = nil
				res_data["status"] = 0
				ngx.print(cjson.encode(res_data))
				-- ngx.say("file upload success")
			end
		elseif type == "eof" then
			break
	end
end

--[[ 	local typ, res, err = form:read()
	ngx.say("read: ", cjson.encode({typ, res}))

	if i==0 then
		ngx.say("please upload at least one file!")
		return
	end ]]
end

--main

local http_method = ngx.req.get_method()

if http_method == "GET" then
	ngx.log(ngx.ERR, "a GET req")
	local args = ngx.req.get_uri_args()
	for key, val in pairs(args) do
		if type(val) == "table" then
			ngx.log(ngx.ERR, "not support")
			ngx.exit(403)
		else
			if key == "action" then
				if val == "get_server_file_list" then
					get_server_file_list(args["path"])
				end
				
				if val == "mkdir" then
					mkdir(args["dir"])
				end
				
				if val == "rm_file" then
					rm_file(args["dir"])
				end
				
				if val == "rename" then
					rename(args["old_dir"], args["new_dir"])
				end
			end
		end
	end
end


if http_method == "POST" then
	ngx.log(ngx.ERR, "a POST req")
	local args = ngx.req.get_uri_args()
	for key, val in pairs(args) do
		if type(val) == "table" then
			ngx.log(ngx.ERR, "not support")
			ngx.exit(403)
		else
			if key == "action" then
				if val == "upload" then
					ngx.log(ngx.ERR, args["path"])
					upload_file(args["path"])
				end
			end
		end
	end
end
