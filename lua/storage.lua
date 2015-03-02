
local BASE_DIR = "/home/data"

local lfs = require("lfs")
local cjson = require("cjson")

local file_list_data = {}

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
	return string.sub(str,findx(str,"/") + 1,-1)
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
				
				t["path"] = relative_path  .. f
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
	scan_dir(relative_path)
	ngx.log(ngx.ERR, cjson.encode(file_list_data))
	ngx.print(cjson.encode(file_list_data))
end


local function mkdir(dir)
	err = lfs.mkdir(BASE_DIR .. dir)
	if err == true then
		ngx.print("ok")
	end
	ngx.print("ok")
end

local function rm_file(path)
	
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
					rm_file()
				end
			end
		end
	end
end


if http_method == "POST" then
	ngx.log(ngx.ERR, "a POST req")
end
